package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.datastore.UserPreferences
import com.example.core.model.DayOfWeekEnum
import com.example.core.model.NotificationType
import com.example.core.model.SessionStatus
import com.example.core.model.SyncState
import com.example.core.model.TimetableSession
import com.example.core.model.TimyNotification
import com.example.data.repository.TimyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardUiState(
    val selectedDay: DayOfWeekEnum = DayOfWeekEnum.MONDAY,
    val today: DayOfWeekEnum = DayOfWeekEnum.MONDAY,
    val activeSession: TimetableSession? = null,
    val nextSession: TimetableSession? = null,
    val remainingMinutes: Int? = null,
    val sessionsForDay: List<TimetableSession> = emptyList(),
    val daysWithChanges: Set<DayOfWeekEnum> = emptySet(),
    val syncState: SyncState = SyncState.LIVE,
    val unreadCount: Int = 0,
    val cohortName: String = "BSc. Software Engineering · Y3S1",
    val userName: String = "Collins Omondi",
    val foregroundNotification: TimyNotification? = null,
    val isRefreshing: Boolean = false
)

class DashboardViewModel(
    private val repository: TimyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val currentDay = getCurrentDayOfWeek()
    private val _selectedDay = MutableStateFlow(currentDay)
    private val _foregroundNotification = MutableStateFlow<TimyNotification?>(null)
    private val _isRefreshing = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            selectedDay = currentDay,
            today = currentDay
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty("SE-Y3S1")
        }

        // Observe cohort name
        viewModelScope.launch {
            userPreferences.cohortName.collect { name ->
                _uiState.update { it.copy(cohortName = name) }
            }
        }

        // Observe user name
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _uiState.update { it.copy(userName = name) }
            }
        }

        // Observe sync state
        viewModelScope.launch {
            repository.syncState.collect { state ->
                _uiState.update { it.copy(syncState = state) }
            }
        }

        // Observe unread notifications count
        viewModelScope.launch {
            repository.getUnreadNotificationsCount().collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }

        // Observe notifications for foreground banner
        viewModelScope.launch {
            repository.getAllNotifications().collect { notifications ->
                val banner = _foregroundNotification.value ?: notifications.firstOrNull {
                    !it.isRead && (it.type == NotificationType.VENUE_CHANGE || it.type == NotificationType.CANCELLATION)
                }
                _uiState.update { it.copy(foregroundNotification = banner) }
            }
        }

        // Observe day sessions reactively
        viewModelScope.launch {
            _selectedDay.collect { day ->
                val cohortId = userPreferences.cohortId.first()
                repository.getSessionsForDay(cohortId, day).collect { sessions ->
                    val (active, next, remaining) = calculateActiveAndNextSession(sessions)
                    _uiState.update {
                        it.copy(
                            selectedDay = day,
                            today = currentDay,
                            sessionsForDay = sessions,
                            activeSession = active,
                            nextSession = next,
                            remainingMinutes = remaining
                        )
                    }
                }
            }
        }

        // Compute days with changes
        viewModelScope.launch {
            val cohortId = userPreferences.cohortId.first()
            repository.getAllSessionsForCohort(cohortId).collect { allSessions ->
                val changedDays = allSessions
                    .filter { it.status == SessionStatus.RESCHEDULED || it.status == SessionStatus.CANCELLED }
                    .map { it.dayOfWeek }
                    .toSet()
                _uiState.update { it.copy(daysWithChanges = changedDays) }
            }
        }
    }

    fun selectDay(day: DayOfWeekEnum) {
        _selectedDay.value = day
    }

    fun refreshSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val cohortId = userPreferences.cohortId.first()
            repository.syncTimetable(cohortId)
            repository.refreshVenues()
            repository.fetchNotifications()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun dismissForegroundNotification() {
        val current = _uiState.value.foregroundNotification
        if (current != null) {
            viewModelScope.launch {
                repository.markNotificationAsRead(current.id)
                _foregroundNotification.value = null
                _uiState.update { it.copy(foregroundNotification = null) }
            }
        }
    }

    private fun getCurrentDayOfWeek(): DayOfWeekEnum {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> DayOfWeekEnum.MONDAY
            Calendar.TUESDAY -> DayOfWeekEnum.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeekEnum.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeekEnum.THURSDAY
            Calendar.FRIDAY -> DayOfWeekEnum.FRIDAY
            Calendar.SATURDAY -> DayOfWeekEnum.SATURDAY
            else -> DayOfWeekEnum.MONDAY
        }
    }

    private fun calculateActiveAndNextSession(
        sessions: List<TimetableSession>
    ): Triple<TimetableSession?, TimetableSession?, Int?> {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        var active: TimetableSession? = null
        var next: TimetableSession? = null
        var remaining: Int? = null

        val sorted = sessions.sortedBy { it.startTime }

        for (session in sorted) {
            val startTotal = parseTimeToMinutes(session.startTime)
            val endTotal = parseTimeToMinutes(session.endTime)

            if (currentTotalMinutes in startTotal until endTotal && session.status != SessionStatus.CANCELLED) {
                active = session
                remaining = endTotal - currentTotalMinutes
                break
            } else if (currentTotalMinutes < startTotal && session.status != SessionStatus.CANCELLED) {
                if (next == null) {
                    next = session
                }
            }
        }

        if (active == null && next == null && sorted.isNotEmpty()) {
            next = sorted.firstOrNull { it.status != SessionStatus.CANCELLED }
        }

        return Triple(active, next, remaining)
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }
}
