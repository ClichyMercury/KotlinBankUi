package com.finsim.presentation.screens.orders

import com.finsim.data.network.dto.OrderResponse
import kotlin.uuid.Uuid

data class OrdersUiState(
    val orders: List<OrderResponse> = emptyList(),
    val tickerByAssetId: Map<Uuid, String> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
