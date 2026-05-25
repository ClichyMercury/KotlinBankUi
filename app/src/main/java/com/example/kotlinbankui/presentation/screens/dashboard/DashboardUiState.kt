package com.example.kotlinbankui.presentation.screens.dashboard

import com.finsim.data.network.dto.PortfolioResponse
import com.finsim.data.network.dto.UserResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DashboardUiState(
    val portfolio: PortfolioResponse? = null,
    val user: UserResponse? = null,
    val sparklines: Map<Uuid, List<Float>> = emptyMap(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
