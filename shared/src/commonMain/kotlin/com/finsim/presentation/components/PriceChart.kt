package com.finsim.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart

/**
 * Simple line chart of closing prices, M3-themed.
 * `prices` should already be in display order (oldest first).
 */
@Composable
fun PriceChart(
    prices: List<Float>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp),
    @Suppress("UNUSED_PARAMETER") lineColor: Color = MaterialTheme.colorScheme.primary
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(prices) {
        if (prices.isEmpty()) return@LaunchedEffect
        modelProducer.runTransaction {
            lineSeries { series(prices) }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom()
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}
