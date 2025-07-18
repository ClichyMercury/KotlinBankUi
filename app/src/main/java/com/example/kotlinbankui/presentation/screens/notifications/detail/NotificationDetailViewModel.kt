package com.example.kotlinbankui.presentation.screens.notifications.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationDetailUiState(
    val notification: NotificationDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationDetailUiState())
    val uiState: StateFlow<NotificationDetailUiState> = _uiState.asStateFlow()

    fun loadNotificationDetail(notificationId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            // Simuler un appel API avec les données mockées
            val notification = getMockNotification(notificationId)

            if (notification != null) {
                _uiState.value = _uiState.value.copy(
                    notification = notification,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Notification not found",
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load notification details",
                isLoading = false
            )
        }
    }

    fun toggleReadStatus(notificationId: String) {
        _uiState.value.notification?.let { notification ->
            val updatedNotification = notification.copy(isRead = !notification.isRead)
            _uiState.value = _uiState.value.copy(notification = updatedNotification)
        }
    }

    fun deleteNotification(notificationId: String) {
        // TODO: Implement delete functionality
        // For now, just simulate success
    }

    private fun getMockNotification(notificationId: String): NotificationDetail? {
        val mockNotifications = listOf(
            NotificationDetail(
                id = "1",
                title = "Payment Received",
                message = "You have received a payment of $1,200.00 from John Smith. The funds have been added to your account and are now available for use.",
                type = NotificationType.PAYMENT,
                timestamp = "2 hours ago",
                isRead = false,
                priority = NotificationPriority.MEDIUM,
                actionRequired = true,
                relatedTransactionId = "TXN001",
                additionalInfo = mapOf(
                    "Sender" to "John Smith",
                    "Amount" to "$1,200.00",
                    "Reference" to "INV-2024-001",
                    "Fee" to "$0.00"
                )
            ),
            NotificationDetail(
                id = "2",
                title = "Security Alert",
                message = "We detected a login attempt from a new device (iPhone 14, iOS 16.2) from New York, NY. If this wasn't you, please secure your account immediately.",
                type = NotificationType.SECURITY,
                timestamp = "4 hours ago",
                isRead = false,
                priority = NotificationPriority.HIGH,
                actionRequired = true,
                additionalInfo = mapOf(
                    "Device" to "iPhone 14",
                    "OS" to "iOS 16.2",
                    "Location" to "New York, NY",
                    "IP Address" to "192.168.1.xxx",
                    "Time" to "10:30 AM EST"
                )
            ),
            NotificationDetail(
                id = "3",
                title = "Account Update",
                message = "Your account information has been successfully updated. The changes include your phone number and email address.",
                type = NotificationType.ACCOUNT,
                timestamp = "Yesterday",
                isRead = true,
                priority = NotificationPriority.LOW,
                actionRequired = false,
                additionalInfo = mapOf(
                    "Updated Fields" to "Phone, Email",
                    "Updated By" to "User",
                    "Previous Phone" to "•••-•••-1234",
                    "New Phone" to "•••-•••-5678"
                )
            ),
            NotificationDetail(
                id = "4",
                title = "Special Offer",
                message = "Get 0% APR for 12 months on balance transfers. Apply now and save on interest charges. This limited-time offer expires soon.",
                type = NotificationType.PROMOTION,
                timestamp = "2 days ago",
                isRead = false,
                priority = NotificationPriority.MEDIUM,
                actionRequired = false,
                additionalInfo = mapOf(
                    "Offer" to "0% APR for 12 months",
                    "Eligible Amount" to "Up to $10,000",
                    "Expires" to "March 31, 2025",
                    "Terms" to "Subject to credit approval"
                )
            ),
            NotificationDetail(
                id = "5",
                title = "System Maintenance",
                message = "Scheduled maintenance will occur on Sunday, March 15th from 2:00 AM to 4:00 AM EST. Some services may be temporarily unavailable.",
                type = NotificationType.SYSTEM,
                timestamp = "3 days ago",
                isRead = true,
                priority = NotificationPriority.LOW,
                actionRequired = false,
                additionalInfo = mapOf(
                    "Maintenance Window" to "2:00 AM - 4:00 AM EST",
                    "Date" to "Sunday, March 15th",
                    "Affected Services" to "Mobile App, Online Banking",
                    "Duration" to "Approximately 2 hours"
                )
            ),
            NotificationDetail(
                id = "6",
                title = "Payment Reminder",
                message = "Your credit card payment of $245.67 is due in 3 days. Set up autopay to never miss a payment.",
                type = NotificationType.REMINDER,
                timestamp = "1 week ago",
                isRead = true,
                priority = NotificationPriority.MEDIUM,
                actionRequired = true,
                additionalInfo = mapOf(
                    "Due Amount" to "$245.67",
                    "Due Date" to "March 20, 2025",
                    "Minimum Payment" to "$25.00",
                    "Account" to "•••• 1234"
                )
            )
        )

        return mockNotifications.find { it.id == notificationId }
    }
}