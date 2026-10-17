package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.NotificationType
import com.example.core.model.TimyNotification
import com.example.data.repository.TimyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationTab {
    ALL,
    VENUE_MOVES,
    CANCELLATIONS
}

data class NotificationsUiState(
    val selectedTab: NotificationTab = NotificationTab.ALL,
    val notifications: List<TimyNotification> = emptyList(),
    val filteredNotifications: List<TimyNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isRefreshing: Boolean = false
)

class NotificationsViewModel(
    private val repository: TimyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            repository.getAllNotifications().collect { list ->
                _uiState.update { current ->
                    current.copy(
                        notifications = list,
                        filteredNotifications = filterList(list, current.selectedTab)
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getUnreadNotificationsCount().collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }
    }

    fun selectTab(tab: NotificationTab) {
        _uiState.update { current ->
            current.copy(
                selectedTab = tab,
                filteredNotifications = filterList(current.notifications, tab)
            )
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            repository.fetchNotifications()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun filterList(
        list: List<TimyNotification>,
        tab: NotificationTab
    ): List<TimyNotification> {
        return when (tab) {
            NotificationTab.ALL -> list
            NotificationTab.VENUE_MOVES -> list.filter { it.type == NotificationType.VENUE_CHANGE }
            NotificationTab.CANCELLATIONS -> list.filter { it.type == NotificationType.CANCELLATION }
        }
    }
}
