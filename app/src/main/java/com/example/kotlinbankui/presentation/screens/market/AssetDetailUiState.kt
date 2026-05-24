package com.example.kotlinbankui.presentation.screens.market

import com.example.kotlinbankui.data.network.dto.AssetResponse

data class AssetDetailUiState(
    val asset: AssetResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
