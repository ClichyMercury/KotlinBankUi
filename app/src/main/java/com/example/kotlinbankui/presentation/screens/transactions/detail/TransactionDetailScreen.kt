package com.example.kotlinbankui.presentation.screens.transactions.detail

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
import com.example.kotlinbankui.presentation.screens.transactions.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.loadTransactionDetail(transactionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction Details",
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
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = { /* TODO: More options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
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
            uiState.transaction?.let { transaction ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Transaction Header
                    item {
                        TransactionHeader(transaction)
                    }

                    // Transaction Info
                    item {
                        TransactionInfoCard(transaction)
                    }

                    // Transaction Details
                    item {
                        TransactionDetailsCard(transaction)
                    }

                    // Actions
                    item {
                        TransactionActionsCard(transaction)
                    }

                    // Similar Transactions
                    item {
                        SimilarTransactionsSection(uiState.similarTransactions)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionHeader(transaction: TransactionDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.isIncome)
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else
                Color(0xFFF44336).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(transaction.category.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(transaction.category),
                    contentDescription = null,
                    tint = transaction.category.color,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transaction Title
            Text(
                text = transaction.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = transaction.subtitle,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            Text(
                text = transaction.amount,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
            )

            // Status Badge
            if (transaction.status != TransactionStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(status = transaction.status)
            }
        }
    }
}

@Composable
fun TransactionInfoCard(transaction: TransactionDetail) {
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
                text = "Transaction Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            InfoRow(label = "Date", value = transaction.date)
            InfoRow(label = "Time", value = transaction.time)
            InfoRow(label = "Category", value = transaction.category.displayName)
            InfoRow(label = "Transaction ID", value = transaction.id)
            InfoRow(label = "Type", value = if (transaction.isIncome) "Income" else "Expense")
        }
    }
}

@Composable
fun TransactionDetailsCard(transaction: TransactionDetail) {
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
                text = "Payment Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            InfoRow(label = "Payment Method", value = "•••• •••• •••• 1234")
            InfoRow(label = "Reference", value = "REF${transaction.id.uppercase()}")
            InfoRow(label = "Processing Fee", value = if (transaction.isIncome) "$0.00" else "$0.50")

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            InfoRow(
                label = "Total Amount",
                value = transaction.amount,
                isHighlighted = true
            )
        }
    }
}

@Composable
fun TransactionActionsCard(transaction: TransactionDetail) {
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
                text = "Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "Download Receipt",
                    icon = Icons.Default.Download,
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Download receipt */ }
                )

                ActionButton(
                    text = "Report Issue",
                    icon = Icons.Default.Report,
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Report issue */ }
                )
            }

            if (!transaction.isIncome) {
                OutlinedButton(
                    onClick = { /* TODO: Request refund */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Refund")
                }
            }
        }
    }
}

@Composable
fun SimilarTransactionsSection(transactions: List<TransactionDetail>) {
    if (transactions.isNotEmpty()) {
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
                    text = "Similar Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                transactions.take(3).forEach { transaction ->
                    SimilarTransactionItem(transaction)
                    if (transaction != transactions.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = if (isHighlighted) 16.sp else 14.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
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
fun SimilarTransactionItem(transaction: TransactionDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(transaction.category.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(transaction.category),
                contentDescription = null,
                tint = transaction.category.color,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = transaction.date,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Text(
            text = transaction.amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}