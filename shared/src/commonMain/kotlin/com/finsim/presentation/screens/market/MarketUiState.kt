package com.finsim.presentation.screens.market

import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.AssetType
import kotlin.uuid.Uuid

data class MarketUiState(
    val assets: List<AssetResponse> = emptyList(),
    val sparklines: Map<Uuid, List<Float>> = emptyMap(),
    val selectedType: AssetType? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
