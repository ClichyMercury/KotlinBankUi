package com.example.kotlinbankui.presentation.screens.orders

import com.finsim.data.network.dto.OrderResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class OrdersUiState(
    val orders: List<OrderResponse> = emptyList(),
    val tickerByAssetId: Map<Uuid, String> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
