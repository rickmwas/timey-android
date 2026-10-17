package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.datastore.UserPreferences
import com.example.core.model.DayOfWeekEnum
import com.example.core.model.SessionStatus
import com.example.core.model.TimetableSession
import com.example.data.repository.TimyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScheduleFilter {
    ALL,
    CHANGES_ONLY,
    LABS_ONLY
}

data class ScheduleUiState(
    val cohortName: String = "BSc. Software Engineering · Y3S1",
    val selectedDayFilter: DayOfWeekEnum? = null,
    val selectedCategoryFilter: ScheduleFilter = ScheduleFilter.ALL,
    val allSessions: List<TimetableSession> = emptyList(),
    val filteredSessions: List<TimetableSession> = emptyList(),
    val isRefreshing: Boolean = false
)

class ScheduleViewModel(
    private val repository: TimyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadWeeklySchedule()
    }

    private fun loadWeeklySchedule() {
        viewModelScope.launch {
            val cohortId = userPreferences.cohortId.first()
            val cohortName = userPreferences.cohortName.first()

            _uiState.update { it.copy(cohortName = cohortName) }

            repository.getAllSessionsForCohort(cohortId).collect { sessions ->
                _uiState.update { current ->
                    current.copy(
                        allSessions = sessions,
                        filteredSessions = applyFilters(sessions, current.selectedDayFilter, current.selectedCategoryFilter)
                    )
                }
            }
        }
    }

    fun selectDayFilter(day: DayOfWeekEnum?) {
        _uiState.update { current ->
            current.copy(
                selectedDayFilter = day,
                filteredSessions = applyFilters(current.allSessions, day, current.selectedCategoryFilter)
            )
        }
    }

    fun selectCategoryFilter(filter: ScheduleFilter) {
        _uiState.update { current ->
            current.copy(
                selectedCategoryFilter = filter,
                filteredSessions = applyFilters(current.allSessions, current.selectedDayFilter, filter)
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val cohortId = userPreferences.cohortId.first()
            repository.syncTimetable(cohortId)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun applyFilters(
        sessions: List<TimetableSession>,
        dayFilter: DayOfWeekEnum?,
        catFilter: ScheduleFilter
    ): List<TimetableSession> {
        return sessions
            .filter { session ->
                if (dayFilter != null) session.dayOfWeek == dayFilter else true
            }
            .filter { session ->
                when (catFilter) {
                    ScheduleFilter.ALL -> true
                    ScheduleFilter.CHANGES_ONLY -> session.status == SessionStatus.RESCHEDULED || session.status == SessionStatus.CANCELLED
                    ScheduleFilter.LABS_ONLY -> session.venue.contains("Lab", ignoreCase = true)
                }
            }
            .sortedWith(compareBy({ it.dayOfWeek.dayNumber }, { it.startTime }))
    }
}
