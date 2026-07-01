package com.finsim.presentation.screens.trading

import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.OrderResponse
import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class SellUiState(
    val asset: AssetResponse? = null,
    val heldQuantity: BigDecimal? = null,
    val avgBuyPrice: BigDecimal? = null,
    val quantityText: String = "",
    val isLoadingAsset: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val orderPlaced: OrderResponse? = null
) {
    val quantity: BigDecimal? = quantityText.toBigDecimalOrNullSafe()

    val total: BigDecimal? = asset?.lastPrice?.let { price ->
        quantity?.let { qty -> price.multiply(qty) }
    }

    val exceedsHeld: Boolean =
        quantity != null && heldQuantity != null && quantity > heldQuantity

    val canSubmit: Boolean
        get() = !isSubmitting &&
            asset != null &&
            quantity != null &&
            quantity.signum() > 0 &&
            !exceedsHeld
}

private fun String.toBigDecimalOrNullSafe(): BigDecimal? = runCatching {
    if (isBlank()) null else BigDecimal.parseString(replace(',', '.'))
}.getOrNull()
