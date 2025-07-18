package com.example.kotlinbankui.presentation.screens.notifications.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class NotificationDetail(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean,
    val priority: NotificationPriority,
    val actionRequired: Boolean = false,
    val relatedTransactionId: String? = null,
    val additionalInfo: Map<String, String> = emptyMap()
)

enum class NotificationType(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    PAYMENT("Payment", Icons.Default.Payment, Color(0xFF4CAF50)),
    SECURITY("Security", Icons.Default.Security, Color(0xFFF44336)),
    ACCOUNT("Account", Icons.Default.AccountBalance, Color(0xFF2196F3)),
    PROMOTION("Promotion", Icons.Default.LocalOffer, Color(0xFFFF9800)),
    SYSTEM("System", Icons.Default.Info, Color(0xFF9C27B0)),
    REMINDER("Reminder", Icons.Default.Alarm, Color(0xFF607D8B))
}

enum class NotificationPriority {
    LOW, MEDIUM, HIGH, URGENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    notificationId: String,
    viewModel: NotificationDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(notificationId) {
        viewModel.loadNotificationDetail(notificationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        uiState.notification?.let { viewModel.toggleReadStatus(it.id) }
                    }) {
                        Icon(
                            imageVector = if (uiState.notification?.isRead == true)
                                Icons.Default.MarkEmailRead
                            else
                                Icons.Default.MarkEmailUnread,
                            contentDescription = "Mark as read/unread"
                        )
                    }
                    IconButton(onClick = {
                        uiState.notification?.let { viewModel.deleteNotification(it.id) }
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            uiState.notification?.let { notification ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Notification Header
                    item {
                        NotificationHeader(notification)
                    }

                    // Notification Content
                    item {
                        NotificationContentCard(notification)
                    }

                    // Additional Information
                    if (notification.additionalInfo.isNotEmpty()) {
                        item {
                            AdditionalInfoCard(notification.additionalInfo)
                        }
                    }

                    // Actions
                    if (notification.actionRequired) {
                        item {
                            NotificationActionsCard(notification)
                        }
                    }

                    // Related Transaction
                    if (notification.relatedTransactionId != null) {
                        item {
                            RelatedTransactionCard(notification.relatedTransactionId!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHeader(notification: NotificationDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = notification.type.color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(notification.type.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.type.icon,
                    contentDescription = null,
                    tint = notification.type.color,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.type.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = notification.type.color
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    PriorityBadge(priority = notification.priority)
                }

                Text(
                    text = notification.timestamp,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (!notification.isRead) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2196F3).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "NEW",
                            color = Color(0xFF2196F3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationContentCard(notification: NotificationDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = notification.message,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun AdditionalInfoCard(additionalInfo: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Additional Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            additionalInfo.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationActionsCard(notification: NotificationDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Actions Required",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (notification.type) {
                NotificationType.SECURITY -> {
                    SecurityActions()
                }
                NotificationType.PAYMENT -> {
                    PaymentActions()
                }
                NotificationType.ACCOUNT -> {
                    AccountActions()
                }
                else -> {
                    GeneralActions()
                }
            }
        }
    }
}

@Composable
fun RelatedTransactionCard(transactionId: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { /* TODO: Navigate to transaction detail */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Related Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Transaction ID: $transactionId",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: NotificationPriority) {
    val (color, text) = when (priority) {
        NotificationPriority.LOW -> Color(0xFF4CAF50) to "LOW"
        NotificationPriority.MEDIUM -> Color(0xFFFF9800) to "MEDIUM"
        NotificationPriority.HIGH -> Color(0xFFF44336) to "HIGH"
        NotificationPriority.URGENT -> Color(0xFF9C27B0) to "URGENT"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SecurityActions() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { /* TODO: Update security settings */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336)
            )
        ) {
            Text("Update Security Settings")
        }

        OutlinedButton(
            onClick = { /* TODO: Contact support */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Contact Support")
        }
    }
}

@Composable
fun PaymentActions() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { /* TODO: View payment */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Payment Details")
        }

        OutlinedButton(
            onClick = { /* TODO: Download receipt */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download Receipt")
        }
    }
}

@Composable
fun AccountActions() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { /* TODO: View account */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Account Details")
        }

        OutlinedButton(
            onClick = { /* TODO: Update info */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Information")
        }
    }
}

@Composable
fun GeneralActions() {
    OutlinedButton(
        onClick = { /* TODO: Learn more */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Learn More")
    }
}