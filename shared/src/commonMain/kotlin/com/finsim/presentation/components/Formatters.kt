package com.finsim.presentation.components

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode

fun BigDecimal?.formatMoney(currency: String = "$"): String {
    val safe = this ?: return "$currency 0.00"
    return "$currency ${formatWithGrouping(safe.scaleToTwo(), forceMinDecimals = 2)}"
}

fun BigDecimal?.formatMoneySigned(currency: String = "$"): String {
    val safe = this ?: return "$currency 0.00"
    val sign = if (safe.signum() >= 0) "+" else "-"
    return "$sign$currency ${formatWithGrouping(safe.abs().scaleToTwo(), forceMinDecimals = 2)}"
}

fun BigDecimal?.formatQuantity(): String {
    val safe = this ?: return "0"
    return formatWithGrouping(safe.trimToMaxDecimals(8), forceMinDecimals = 0)
}

fun BigDecimal?.formatPercent(): String {
    val safe = this ?: return "0.00%"
    val sign = when {
        safe.signum() > 0 -> "+"
        safe.signum() < 0 -> "-"
        else -> ""
    }
    return "$sign${formatWithGrouping(safe.abs().scaleToTwo(), forceMinDecimals = 2)}%"
}

/** PnL as percentage of cost basis (avg buy price × qty) */
fun computePnlPercent(unrealizedPnl: BigDecimal?, costBasis: BigDecimal?): BigDecimal? {
    if (unrealizedPnl == null || costBasis == null || costBasis.signum() == 0) return null
    val mode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, scale = 4)
    return unrealizedPnl
        .multiply(BigDecimal.fromInt(100))
        .divide(costBasis, mode)
}

// ---- internals ----

private fun BigDecimal.scaleToTwo(): BigDecimal =
    this.roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

private fun BigDecimal.trimToMaxDecimals(maxDecimals: Long): BigDecimal =
    this.roundToDigitPositionAfterDecimalPoint(maxDecimals, RoundingMode.TOWARDS_ZERO)

/**
 * Format a BigDecimal as "1,234,567.89" with thousands grouping (comma) and a
 * `.` decimal separator. If forceMinDecimals > 0, pads decimals with zeros to
 * reach the minimum. Trailing zeros are otherwise stripped.
 */
private fun formatWithGrouping(value: BigDecimal, forceMinDecimals: Int): String {
    val raw = value.toPlainString() // e.g. "-1234567.50" or "0.123456"
    val negative = raw.startsWith('-')
    val unsigned = if (negative) raw.substring(1) else raw
    val dotIdx = unsigned.indexOf('.')
    val intPart = if (dotIdx >= 0) unsigned.substring(0, dotIdx) else unsigned
    var decPart = if (dotIdx >= 0) unsigned.substring(dotIdx + 1) else ""

    // Pad or trim decimal part
    if (decPart.length > forceMinDecimals) {
        // strip trailing zeros down to forceMinDecimals
        var end = decPart.length
        while (end > forceMinDecimals && decPart[end - 1] == '0') end--
        decPart = decPart.substring(0, end)
    }
    if (decPart.length < forceMinDecimals) {
        decPart = decPart.padEnd(forceMinDecimals, '0')
    }

    // Insert commas every 3 digits in integer part (from right)
    val grouped = buildString {
        val len = intPart.length
        for (i in 0 until len) {
            val fromRight = len - i
            append(intPart[i])
            if (fromRight > 1 && fromRight % 3 == 1) append(',')
        }
    }

    val result = if (decPart.isEmpty()) grouped else "$grouped.$decPart"
    return if (negative) "-$result" else result
}
