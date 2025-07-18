package com.example.kotlinbankui.presentation.screens.transactions

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.BottomNavigationBar
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes

data class TransactionDetail(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val isIncome: Boolean,
    val date: String,
    val time: String,
    val category: TransactionCategory,
    val status: TransactionStatus = TransactionStatus.COMPLETED
)

enum class TransactionCategory(val displayName: String, val color: Color) {
    FOOD("Food", Color(0xFFFF9800)),
    TRANSPORT("Transport", Color(0xFF2196F3)),
    SHOPPING("Shopping", Color(0xFF9C27B0)),
    ENTERTAINMENT("Entertainment", Color(0xFFE91E63)),
    SALARY("Salary", Color(0xFF4CAF50)),
    TRANSFER("Transfer", Color(0xFF00BCD4)),
    BILLS("Bills", Color(0xFFF44336)),
    OTHER("Other", Color(0xFF607D8B))
}

enum class TransactionStatus {
    COMPLETED, PENDING, FAILED
}

data class FilterChip(
    val label: String,
    val isSelected: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Income", "Expenses", "Today", "This Week")

    val transactions = getTransactions()

    val filteredTransactions = remember(selectedFilter, transactions) {
        when (selectedFilter) {
            "Income" -> transactions.filter { it.isIncome }
            "Expenses" -> transactions.filter { !it.isIncome }
            "Today" -> transactions.filter { it.date == "Today" }
            "This Week" -> transactions.filter {
                it.date in listOf("Today", "Yesterday", "2 days ago", "3 days ago")
            }
            else -> transactions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add transaction */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            item {
                TransactionSummaryCard()
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filterOptions) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
            }

            // Transactions by Date
            val groupedTransactions = filteredTransactions.groupBy { it.date }

            items(groupedTransactions.keys.toList()) { date ->
                Column {
                    // Date Header
                    Text(
                        text = date,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Transactions for this date
                    groupedTransactions[date]?.forEach { transaction ->
                        TransactionDetailItem(
                            transaction = transaction,
                            onClick = {  navController.navigate(NavigationRoutes.transactionDetail(transaction.id)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                title = "Income",
                amount = "+$12,450",
                color = Color(0xFF4CAF50),
                icon = Icons.Default.TrendingUp
            )

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            SummaryItem(
                title = "Expenses",
                amount = "-$8,239",
                color = Color(0xFFF44336),
                icon = Icons.Default.TrendingDown
            )
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    amount: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun TransactionDetailItem(
    transaction: TransactionDetail,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
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
            // Category Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(transaction.category.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(transaction.category),
                    contentDescription = null,
                    tint = transaction.category.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Transaction Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = transaction.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = transaction.subtitle,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        // Status Badge
                        if (transaction.status != TransactionStatus.COMPLETED) {
                            StatusBadge(status = transaction.status)
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
    }
}

@Composable
fun StatusBadge(status: TransactionStatus) {
    val (color, text) = when (status) {
        TransactionStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        TransactionStatus.FAILED -> Color(0xFFF44336) to "Failed"
        TransactionStatus.COMPLETED -> Color(0xFF4CAF50) to "Completed"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

fun getCategoryIcon(category: TransactionCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        TransactionCategory.FOOD -> Icons.Default.Restaurant
        TransactionCategory.TRANSPORT -> Icons.Default.DirectionsCar
        TransactionCategory.SHOPPING -> Icons.Default.ShoppingBag
        TransactionCategory.ENTERTAINMENT -> Icons.Default.Movie
        TransactionCategory.SALARY -> Icons.Default.AccountBalance
        TransactionCategory.TRANSFER -> Icons.Default.SwapHoriz
        TransactionCategory.BILLS -> Icons.Default.Receipt
        TransactionCategory.OTHER -> Icons.Default.Category
    }
}

fun getTransactions(): List<TransactionDetail> {
    return listOf(
        TransactionDetail("1", "Netflix Subscription", "Monthly payment", "-$15.99", false, "Today", "2:30 PM", TransactionCategory.ENTERTAINMENT),
        TransactionDetail("2", "Uber Ride", "To downtown", "-$12.50", false, "Today", "10:15 AM", TransactionCategory.TRANSPORT),
        TransactionDetail("3", "Salary Deposit", "Monthly salary", "+$3,450.00", true, "Yesterday", "9:00 AM", TransactionCategory.SALARY),
        TransactionDetail("4", "Amazon Purchase", "Electronics", "-$67.85", false, "Yesterday", "3:45 PM", TransactionCategory.SHOPPING),
        TransactionDetail("5", "McDonald's", "Fast food", "-$8.99", false, "2 days ago", "12:30 PM", TransactionCategory.FOOD),
        TransactionDetail("6", "Transfer from John", "Personal transfer", "+$120.00", true, "2 days ago", "6:20 PM", TransactionCategory.TRANSFER),
        TransactionDetail("7", "Electricity Bill", "Monthly bill", "-$89.50", false, "3 days ago", "11:00 AM", TransactionCategory.BILLS),
        TransactionDetail("8", "Freelance Payment", "Design work", "+$850.00", true, "3 days ago", "4:15 PM", TransactionCategory.SALARY),
        TransactionDetail("9", "Spotify Premium", "Music subscription", "-$9.99", false, "4 days ago", "7:00 AM", TransactionCategory.ENTERTAINMENT, TransactionStatus.PENDING),
        TransactionDetail("10", "Grocery Store", "Weekly shopping", "-$125.43", false, "5 days ago", "6:30 PM", TransactionCategory.FOOD),
    )
}