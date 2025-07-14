package com.example.kotlinbankui.presentation.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        // Données mockées pour l'instant
        val mockTransactions = listOf(
            RecentTransaction("1", "Netflix", "-$15.99", "2h ago", false),
            RecentTransaction("2", "Salary", "+$3,450", "1 day ago", true),
            RecentTransaction("3", "Amazon", "-$67.85", "2 days ago", false)
        )

        _uiState.value = _uiState.value.copy(
            recentTransactions = mockTransactions,
            greeting = getGreeting()
        )
    }

    private fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    fun toggleBalanceVisibility() {
        _uiState.value = _uiState.value.copy(
            balanceVisible = !_uiState.value.balanceVisible
        )
    }

    fun refreshData() {
        loadHomeData()
    }
}