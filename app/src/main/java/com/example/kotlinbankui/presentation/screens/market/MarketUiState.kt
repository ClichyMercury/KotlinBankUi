package com.example.kotlinbankui.presentation.screens.market

import com.example.kotlinbankui.data.network.dto.AssetResponse
import com.example.kotlinbankui.data.network.dto.AssetType
import java.util.UUID

data class MarketUiState(
    val assets: List<AssetResponse> = emptyList(),
    val sparklines: Map<UUID, List<Float>> = emptyMap(),
    val selectedType: AssetType? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
