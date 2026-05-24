package com.example.kotlinbankui.presentation.screens.dashboard

import com.example.kotlinbankui.data.network.dto.PortfolioResponse

data class DashboardUiState(
    val portfolio: PortfolioResponse? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
