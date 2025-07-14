package com.example.kotlinbankui.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.BottomNavigationBar

data class BankNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val isImportant: Boolean = false,
    val amount: String? = null,
    val actionRequired: Boolean = false
)

enum class NotificationType(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    TRANSACTION("Transaction", Icons.Default.Payment, Color(0xFF2196F3)),
    SECURITY("Security", Icons.Default.Security, Color(0xFFF44336)),
    PROMOTION("Promotion", Icons.Default.LocalOffer, Color(0xFF4CAF50)),
    REMINDER("Reminder", Icons.Default.Notifications, Color(0xFFFF9800)),
    SYSTEM("System", Icons.Default.Info, Color(0xFF607D8B)),
    CARD("Card", Icons.Default.CreditCard, Color(0xFF9C27B0))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("All") }
    var showUnreadOnly by remember { mutableStateOf(false) }

    val notifications = getNotifications()
    val filterOptions = listOf("All") + NotificationType.values().map { it.displayName }

    val filteredNotifications = remember(selectedFilter, showUnreadOnly, notifications) {
        var filtered = notifications

        if (selectedFilter != "All") {
            filtered = filtered.filter { it.type.displayName == selectedFilter }
        }

        if (showUnreadOnly) {
            filtered = filtered.filter { !it.isRead }
        }

        filtered
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        if (unreadCount > 0) {
                            Text(
                                text = "$unreadCount unread",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Toggle unread filter
                    IconButton(
                        onClick = { showUnreadOnly = !showUnreadOnly }
                    ) {
                        Icon(
                            imageVector = if (showUnreadOnly) Icons.Default.MarkEmailRead else Icons.Default.MarkEmailUnread,
                            contentDescription = "Filter unread",
                            tint = if (showUnreadOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Mark all as read
                    IconButton(onClick = { /* TODO: Mark all as read */ }) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark all as read"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Quick Stats
            if (unreadCount > 0) {
                NotificationStatsCard(
                    unreadCount = unreadCount,
                    importantCount = notifications.count { it.isImportant && !it.isRead },
                    actionRequiredCount = notifications.count { it.actionRequired && !it.isRead }
                )
            }

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            // Notifications List
            if (filteredNotifications.isEmpty()) {
                EmptyNotificationsState(
                    isFiltered = selectedFilter != "All" || showUnreadOnly
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Group by date
                    val groupedNotifications = filteredNotifications.groupBy { getDateGroup(it.timestamp) }

                    items(groupedNotifications.keys.toList()) { dateGroup ->
                        Column {
                            // Date Header
                            Text(
                                text = dateGroup,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Notifications for this date
                            groupedNotifications[dateGroup]?.forEach { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onClick = { /* TODO: Handle notification click */ },
                                    onMarkAsRead = { /* TODO: Mark as read */ }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationStatsCard(
    unreadCount: Int,
    importantCount: Int,
    actionRequiredCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Notifications,
                count = unreadCount,
                label = "Unread",
                color = MaterialTheme.colorScheme.primary
            )

            if (importantCount > 0) {
                StatItem(
                    icon = Icons.Default.PriorityHigh,
                    count = importantCount,
                    label = "Important",
                    color = Color(0xFFF44336)
                )
            }

            if (actionRequiredCount > 0) {
                StatItem(
                    icon = Icons.Default.TouchApp,
                    count = actionRequiredCount,
                    label = "Action Required",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NotificationItem(
    notification: BankNotification,
    onClick: () -> Unit,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(notification.type.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.type.icon,
                    contentDescription = null,
                    tint = notification.type.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Notification Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Title with badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notification.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (notification.isImportant) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF44336),
                                    modifier = Modifier.size(8.dp)
                                ) {}
                            }

                            if (!notification.isRead) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = notification.message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )

                        // Amount if present
                        notification.amount?.let { amount ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = amount,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (amount.startsWith("+")) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }

                        // Action Required Badge
                        if (notification.actionRequired) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFF9800).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Action Required",
                                    color = Color(0xFFFF9800),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Timestamp
                    Text(
                        text = notification.timestamp,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState(isFiltered: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isFiltered) Icons.Default.FilterList else Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isFiltered) "No notifications match your filter" else "No notifications yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isFiltered) "Try adjusting your filters" else "You're all caught up!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

fun getDateGroup(timestamp: String): String {
    return when {
        timestamp.contains("min") || timestamp.contains("hour") -> "Today"
        timestamp.contains("1 day") -> "Yesterday"
        timestamp.contains("day") -> "This Week"
        else -> "Earlier"
    }
}

fun getNotifications(): List<BankNotification> {
    return listOf(
        BankNotification(
            id = "1",
            title = "Payment Received",
            message = "You received a payment from John Doe",
            type = NotificationType.TRANSACTION,
            timestamp = "5 min ago",
            amount = "+$1,250.00",
            isRead = false
        ),
        BankNotification(
            id = "2",
            title = "Security Alert",
            message = "New device login detected from iPhone. If this wasn't you, please secure your account immediately.",
            type = NotificationType.SECURITY,
            timestamp = "1 hour ago",
            isRead = false,
            isImportant = true,
            actionRequired = true
        ),
        BankNotification(
            id = "3",
            title = "Card Payment",
            message = "Payment processed at Amazon.com",
            type = NotificationType.TRANSACTION,
            timestamp = "2 hours ago",
            amount = "-$67.99",
            isRead = false
        ),
        BankNotification(
            id = "4",
            title = "Special Offer",
            message = "Get 2% cashback on all purchases this month! Limited time offer.",
            type = NotificationType.PROMOTION,
            timestamp = "3 hours ago",
            isRead = true
        ),
        BankNotification(
            id = "5",
            title = "Bill Reminder",
            message = "Your electricity bill of $89.50 is due tomorrow",
            type = NotificationType.REMINDER,
            timestamp = "1 day ago",
            isRead = false,
            actionRequired = true
        ),
        BankNotification(
            id = "6",
            title = "Card Expiring Soon",
            message = "Your Visa card ending in 5154 expires next month. Order a replacement card.",
            type = NotificationType.CARD,
            timestamp = "2 days ago",
            isRead = true,
            actionRequired = true
        ),
        BankNotification(
            id = "7",
            title = "System Maintenance",
            message = "Scheduled maintenance on Sunday 2-4 AM. Some services may be temporarily unavailable.",
            type = NotificationType.SYSTEM,
            timestamp = "3 days ago",
            isRead = true
        ),
        BankNotification(
            id = "8",
            title = "Large Transaction",
            message = "Large transaction detected: $850.00 transfer to your account",
            type = NotificationType.TRANSACTION,
            timestamp = "5 days ago",
            amount = "+$850.00",
            isRead = true,
            isImportant = true
        )
    )
}