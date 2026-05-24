package com.example.kotlinbankui.presentation.screens.dashboard

import com.example.kotlinbankui.data.network.dto.PortfolioResponse
import com.example.kotlinbankui.data.network.dto.UserResponse
import java.util.UUID

data class DashboardUiState(
    val portfolio: PortfolioResponse? = null,
    val user: UserResponse? = null,
    val sparklines: Map<UUID, List<Float>> = emptyMap(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
