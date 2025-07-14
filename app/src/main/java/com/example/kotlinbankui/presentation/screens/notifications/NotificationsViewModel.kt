package com.example.kotlinbankui.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val notifications = getNotifications()
                updateNotificationsState(notifications)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun updateNotificationsState(notifications: List<BankNotification>) {
        val unreadCount = notifications.count { !it.isRead }
        val importantCount = notifications.count { it.isImportant && !it.isRead }
        val actionRequiredCount = notifications.count { it.actionRequired && !it.isRead }

        _uiState.value = _uiState.value.copy(
            notifications = notifications,
            unreadCount = unreadCount,
            importantCount = importantCount,
            actionRequiredCount = actionRequiredCount,
            isLoading = false,
            error = null
        )
    }

    fun updateFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun toggleUnreadFilter() {
        _uiState.value = _uiState.value.copy(
            showUnreadOnly = !_uiState.value.showUnreadOnly
        )
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val updatedNotifications = _uiState.value.notifications.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            updateNotificationsState(updatedNotifications)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val updatedNotifications = _uiState.value.notifications.map { notification ->
                notification.copy(isRead = true)
            }
            updateNotificationsState(updatedNotifications)
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val updatedNotifications = _uiState.value.notifications.filter {
                it.id != notificationId
            }
            updateNotificationsState(updatedNotifications)
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }
}