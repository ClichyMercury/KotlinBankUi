package com.example.kotlinbankui.presentation.screens.market

import com.example.kotlinbankui.data.network.dto.AssetResponse
import com.example.kotlinbankui.data.network.dto.AssetType

data class MarketUiState(
    val assets: List<AssetResponse> = emptyList(),
    val selectedType: AssetType? = null,   // null = All
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
