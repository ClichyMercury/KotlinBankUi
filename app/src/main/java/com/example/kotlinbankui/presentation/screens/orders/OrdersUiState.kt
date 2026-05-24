package com.example.kotlinbankui.presentation.screens.orders

import com.example.kotlinbankui.data.network.dto.OrderResponse
import java.util.UUID

data class OrdersUiState(
    val orders: List<OrderResponse> = emptyList(),
    val tickerByAssetId: Map<UUID, String> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
