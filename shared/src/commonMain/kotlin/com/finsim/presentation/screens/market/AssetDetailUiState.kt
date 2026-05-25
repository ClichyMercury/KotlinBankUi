package com.finsim.presentation.screens.market

import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.CandleResponse

enum class CandlePeriod(val days: Int, val label: String) {
    DAY(1, "1J"),
    WEEK(7, "1S"),
    MONTH(30, "1M"),
    YEAR(365, "1A")
}

data class AssetDetailUiState(
    val asset: AssetResponse? = null,
    val candles: List<CandleResponse> = emptyList(),
    val selectedPeriod: CandlePeriod = CandlePeriod.WEEK,
    val isLoading: Boolean = false,
    val isLoadingCandles: Boolean = false,
    val errorMessage: String? = null,
    val candlesErrorMessage: String? = null
)
