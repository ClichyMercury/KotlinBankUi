package com.example.kotlinbankui.presentation.components.finsim

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val moneySymbols = DecimalFormatSymbols(Locale.US).apply {
    groupingSeparator = ','
    decimalSeparator = '.'
}
private val moneyFormat = DecimalFormat("#,##0.00", moneySymbols)
private val quantityFormat = DecimalFormat("#,##0.########", moneySymbols)
private val percentFormat = DecimalFormat("0.00", moneySymbols)

fun BigDecimal?.formatMoney(currency: String = "$"): String {
    val safe = this ?: return "$currency 0.00"
    return "$currency ${moneyFormat.format(safe.setScale(2, RoundingMode.HALF_UP))}"
}

fun BigDecimal?.formatMoneySigned(currency: String = "$"): String {
    val safe = this ?: return "$currency 0.00"
    val sign = if (safe.signum() >= 0) "+" else "-"
    return "$sign$currency ${moneyFormat.format(safe.abs().setScale(2, RoundingMode.HALF_UP))}"
}

fun BigDecimal?.formatQuantity(): String {
    val safe = this ?: return "0"
    return quantityFormat.format(safe.stripTrailingZeros())
}

fun BigDecimal?.formatPercent(): String {
    val safe = this ?: return "0.00%"
    val sign = if (safe.signum() > 0) "+" else if (safe.signum() < 0) "-" else ""
    return "$sign${percentFormat.format(safe.abs())}%"
}

/** PnL as percentage of cost basis (avg buy price × qty) */
fun computePnlPercent(unrealizedPnl: BigDecimal?, costBasis: BigDecimal?): BigDecimal? {
    if (unrealizedPnl == null || costBasis == null || costBasis.signum() == 0) return null
    return unrealizedPnl
        .multiply(BigDecimal(100))
        .divide(costBasis, 4, RoundingMode.HALF_UP)
}
