package com.example.kotlinbankui.presentation.screens.transactions

data class TransactionsUiState(
    val transactions: List<TransactionDetail> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val selectedFilter: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)
