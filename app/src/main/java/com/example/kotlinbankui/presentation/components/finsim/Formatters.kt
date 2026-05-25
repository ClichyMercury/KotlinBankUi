package com.example.kotlinbankui.presentation.components.finsim

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import java.math.BigDecimal as JavaBigDecimal
import java.math.RoundingMode as JavaRoundingMode
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

// Bridge ionspin BigDecimal → java.math.BigDecimal so we can keep DecimalFormat in :app.
// When UI moves to commonMain (Phase 3) this bridge disappears in favor of a KMP-pure formatter.
private fun BigDecimal.toJavaBd(): JavaBigDecimal = JavaBigDecimal(this.toPlainString())

fun BigDecimal?.formatMoney(currency: String = "$"): String {
    val safe = this ?: return "$currency 0.00"
    val rounded = safe.toJavaBd().setScale(2, JavaRoundingMode.HALF_UP)
    return "$currency ${moneyFormat.format(rounded)}"
}

fun BigDecimal?.formatMoneySigned(currency: String = "$"): String {
    val safe = this ?: return "$currency 0.00"
    val sign = if (safe.signum() >= 0) "+" else "-"
    val rounded = safe.abs().toJavaBd().setScale(2, JavaRoundingMode.HALF_UP)
    return "$sign$currency ${moneyFormat.format(rounded)}"
}

fun BigDecimal?.formatQuantity(): String {
    val safe = this ?: return "0"
    return quantityFormat.format(safe.toJavaBd().stripTrailingZeros())
}

fun BigDecimal?.formatPercent(): String {
    val safe = this ?: return "0.00%"
    val sign = when {
        safe.signum() > 0 -> "+"
        safe.signum() < 0 -> "-"
        else -> ""
    }
    return "$sign${percentFormat.format(safe.abs().toJavaBd())}%"
}

/** PnL as percentage of cost basis (avg buy price × qty) */
fun computePnlPercent(unrealizedPnl: BigDecimal?, costBasis: BigDecimal?): BigDecimal? {
    if (unrealizedPnl == null || costBasis == null || costBasis.signum() == 0) return null
    val mode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, scale = 4)
    return unrealizedPnl
        .multiply(BigDecimal.fromInt(100))
        .divide(costBasis, mode)
}
