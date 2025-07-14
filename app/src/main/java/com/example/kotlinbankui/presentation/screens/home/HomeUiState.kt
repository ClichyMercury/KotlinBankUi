package com.example.kotlinbankui.presentation.screens.home

data class HomeUiState(
    val userName: String = "John Doe",
    val greeting: String = "Good Morning",
    val totalBalance: String = "$ 77,584",
    val balanceVisible: Boolean = true,
    val income: String = "+$12,450",
    val expenses: String = "-$8,239",
    val unreadNotifications: Int = 3,
    val recentTransactions: List<RecentTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RecentTransaction(
    val id: String,
    val title: String,
    val amount: String,
    val time: String,
    val isIncome: Boolean
)