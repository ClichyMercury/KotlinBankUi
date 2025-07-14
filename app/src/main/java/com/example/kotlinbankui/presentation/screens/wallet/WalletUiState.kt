package com.example.kotlinbankui.presentation.screens.wallet

data class WalletUiState(
    val totalBalance: Double = 77584.0,
    val income: Double = 12450.0,
    val expenses: Double = 8239.0,
    val savings: Double = 45320.0,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)