package com.example.kotlinbankui.presentation.screens.trading

import com.example.kotlinbankui.data.network.dto.AssetResponse
import com.example.kotlinbankui.data.network.dto.OrderResponse
import java.math.BigDecimal

data class BuyUiState(
    val asset: AssetResponse? = null,
    val quantityText: String = "",
    val isLoadingAsset: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val orderPlaced: OrderResponse? = null
) {
    val quantity: BigDecimal? = quantityText.toBigDecimalOrNullSafe()
    val total: BigDecimal? = if (quantity != null && asset?.lastPrice != null)
        asset.lastPrice.multiply(quantity)
    else null

    val canSubmit: Boolean
        get() = !isSubmitting && asset != null && quantity != null && quantity.signum() > 0
}

private fun String.toBigDecimalOrNullSafe(): BigDecimal? = runCatching {
    if (isBlank()) null else BigDecimal(replace(',', '.'))
}.getOrNull()
