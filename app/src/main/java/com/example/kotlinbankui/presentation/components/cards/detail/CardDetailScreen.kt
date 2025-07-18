package com.example.kotlinbankui.presentation.components.cards.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.screens.cards.detail.CardDetailViewModel
import com.example.kotlinbankui.presentation.screens.transactions.detail.InfoRow

data class CardDetail(
    val id: String,
    val cardName: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val cardType: CardType,
    val balance: String,
    val availableCredit: String,
    val isActive: Boolean = true,
    val isBlocked: Boolean = false
)

enum class CardType(val displayName: String, val colors: List<Color>) {
    VISA("Visa", listOf(Color(0xFF1A237E), Color(0xFF3F51B5))),
    MASTERCARD("Mastercard", listOf(Color(0xFFD32F2F), Color(0xFFFF5722))),
    AMERICAN_EXPRESS("American Express", listOf(Color(0xFF388E3C), Color(0xFF66BB6A))),
    DISCOVER("Discover", listOf(Color(0xFFFF6F00), Color(0xFFFFB300)))
}

data class CardTransaction(
    val id: String,
    val merchant: String,
    val amount: String,
    val date: String,
    val time: String,
    val category: String,
    val isIncome: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    navController: NavController,
    cardId: String,
    viewModel: CardDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(cardId) {
        viewModel.loadCardDetail(cardId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Card Details",
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
                    IconButton(onClick = { /* TODO: Card settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
            uiState.card?.let { card ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card Visual
                    item {
                        CardVisual(card)
                    }

                    // Card Info
                    item {
                        CardInfoSection(card)
                    }

                    // Quick Actions
                    item {
                        CardActionsSection(card)
                    }

                    // Card Statistics
                    item {
                        CardStatisticsSection(card)
                    }

                    // Recent Transactions
                    item {
                        Text(
                            text = "Recent Transactions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(uiState.recentTransactions) { transaction ->
                        CardTransactionItem(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun CardVisual(card: CardDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(card.cardType.colors),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                // Card Type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = card.cardType.displayName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (card.isBlocked) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Red.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "BLOCKED",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Card Number
                Text(
                    text = formatCardNumber(card.cardNumber),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Card Info Bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CARD HOLDER",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = card.cardName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column {
                        Text(
                            text = "EXPIRES",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = card.expiryDate,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardInfoSection(card: CardDetail) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Card Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            InfoRow(label = "Card Name", value = card.cardName)
            InfoRow(label = "Card Number", value = "•••• •••• •••• ${card.cardNumber.takeLast(4)}")
            InfoRow(label = "Expiry Date", value = card.expiryDate)
            InfoRow(label = "CVV", value = "•••")
            InfoRow(label = "Status", value = if (card.isActive) "Active" else "Inactive")
        }
    }
}

@Composable
fun CardActionsSection(card: CardDetail) {
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
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardActionButton(
                    text = if (card.isBlocked) "Unblock" else "Block",
                    icon = if (card.isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    color = if (card.isBlocked) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Block/Unblock card */ }
                )

                CardActionButton(
                    text = "Freeze",
                    icon = Icons.Default.Pause,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Freeze card */ }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardActionButton(
                    text = "Replace",
                    icon = Icons.Default.CreditCard,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Replace card */ }
                )

                CardActionButton(
                    text = "PIN Change",
                    icon = Icons.Default.Key,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Change PIN */ }
                )
            }
        }
    }
}

@Composable
fun CardStatisticsSection(card: CardDetail) {
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
                text = "Card Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = "Balance",
                    value = card.balance,
                    color = Color(0xFF4CAF50)
                )

                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                StatisticItem(
                    title = "Available Credit",
                    value = card.availableCredit,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
fun CardActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun StatisticItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun CardTransactionItem(transaction: CardTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (transaction.isIncome)
                            Color(0xFF4CAF50).copy(alpha = 0.1f)
                        else
                            Color(0xFFF44336).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.isIncome)
                        Icons.Default.ArrowDownward
                    else
                        Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Row {
                    Text(
                        text = transaction.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = " • ${transaction.date}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = transaction.amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Text(
                    text = transaction.time,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private fun formatCardNumber(cardNumber: String): String {
    return cardNumber.chunked(4).joinToString(" ")
}