package com.example.kotlinbankui.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow



class TransactionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        val transactions = getTransactions()
        val income = transactions.filter { it.isIncome }
            .sumOf { it.amount.replace("[+$,-]".toRegex(), "").toDoubleOrNull() ?: 0.0 }
        val expenses = transactions.filter { !it.isIncome }
            .sumOf { it.amount.replace("[+$,-]".toRegex(), "").toDoubleOrNull() ?: 0.0 }

        _uiState.value = _uiState.value.copy(
            transactions = transactions,
            totalIncome = income,
            totalExpenses = expenses
        )
    }

    fun updateFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun refreshTransactions() {
        loadTransactions()
    }
}