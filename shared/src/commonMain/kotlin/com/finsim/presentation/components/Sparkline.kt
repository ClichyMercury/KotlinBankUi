package com.finsim.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.finsim.ui.theme.TrendDown
import com.finsim.ui.theme.TrendNeutral
import com.finsim.ui.theme.TrendUp

/**
 * Compact line chart for inline usage (market list, position rows, summary cards).
 * No axes, no labels, just the trend.
 *
 * The color is auto-derived from the first vs last value (up = green, down = red,
 * flat = neutral) unless overridden.
 */
@Composable
fun Sparkline(
    values: List<Float>,
    modifier: Modifier = Modifier.size(width = 88.dp, height = 36.dp),
    color: Color? = null,
    strokeWidth: Float = 2f,
    fillBelow: Boolean = true
) {
    if (values.size < 2) return

    val first = values.first()
    val last = values.last()
    val effectiveColor = color ?: when {
        last > first -> TrendUp
        last < first -> TrendDown
        else -> TrendNeutral
    }

    Canvas(modifier = modifier) {
        val min = values.min()
        val max = values.max()
        val range = (max - min).coerceAtLeast(0.0001f)
        val stepX = if (values.size > 1) size.width / (values.size - 1) else 0f
        val verticalPadding = strokeWidth.dp.toPx()
        val usableHeight = size.height - verticalPadding * 2

        val path = Path()
        val fillPath = Path()

        values.forEachIndexed { i, v ->
            val x = i * stepX
            val y = verticalPadding + usableHeight - ((v - min) / range) * usableHeight
            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        if (fillBelow) {
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(effectiveColor.copy(alpha = 0.25f), Color.Transparent),
                    startY = 0f,
                    endY = size.height
                )
            )
        }

        drawPath(
            path = path,
            color = effectiveColor,
            style = Stroke(
                width = strokeWidth.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.cornerPathEffect(2f)
            )
        )
    }
}
