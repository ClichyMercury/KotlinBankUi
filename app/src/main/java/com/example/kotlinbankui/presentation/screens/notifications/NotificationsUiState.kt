package com.example.kotlinbankui.presentation.screens.notifications

data class NotificationsUiState(
    val notifications: List<BankNotification> = emptyList(),
    val unreadCount: Int = 0,
    val importantCount: Int = 0,
    val actionRequiredCount: Int = 0,
    val selectedFilter: String = "All",
    val showUnreadOnly: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)