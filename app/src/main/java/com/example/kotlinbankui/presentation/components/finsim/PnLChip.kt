package com.example.kotlinbankui.presentation.components.finsim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlinbankui.ui.theme.TrendDown
import com.example.kotlinbankui.ui.theme.TrendNeutral
import com.example.kotlinbankui.ui.theme.TrendUp
import com.ionspin.kotlin.bignum.decimal.BigDecimal

@Composable
fun PnLChip(
    value: BigDecimal?,
    modifier: Modifier = Modifier,
    isPercent: Boolean = false
) {
    val sign = value?.signum() ?: 0
    val color: Color = when {
        sign > 0 -> TrendUp
        sign < 0 -> TrendDown
        else -> TrendNeutral
    }
    val icon = when {
        sign > 0 -> Icons.AutoMirrored.Filled.TrendingUp
        sign < 0 -> Icons.AutoMirrored.Filled.TrendingDown
        else -> Icons.AutoMirrored.Filled.TrendingFlat
    }
    val label = if (isPercent) value.formatPercent() else value.formatMoneySigned()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}
