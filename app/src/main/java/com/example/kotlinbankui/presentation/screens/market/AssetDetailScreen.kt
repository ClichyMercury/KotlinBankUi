package com.example.kotlinbankui.presentation.screens.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.CandleResponse
import com.example.kotlinbankui.presentation.components.finsim.AssetAvatar
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.OrderButton
import com.example.kotlinbankui.presentation.components.finsim.OrderButtonStyle
import com.example.kotlinbankui.presentation.components.finsim.PnLChip
import com.example.kotlinbankui.presentation.components.finsim.PriceChart
import com.example.kotlinbankui.presentation.components.finsim.formatMoney
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.example.kotlinbankui.ui.theme.MoneyHero
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun AssetDetailScreen(
    navController: NavController,
    assetId: String,
    viewModel: AssetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinSimTopBar(
                title = state.asset?.ticker ?: "Actif",
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            state.asset?.let {
                StickyBuyBar(
                    ticker = it.ticker,
                    onBuy = { navController.navigate(NavigationRoutes.buy(assetId)) }
                )
            }
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(modifier = Modifier.padding(padding))

            state.asset == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                ErrorBanner(
                    message = state.errorMessage ?: "Actif introuvable",
                    onRetry = { viewModel.load(assetId) }
                )
            }

            else -> AssetDetailContent(
                asset = state.asset!!,
                candles = state.candles,
                selectedPeriod = state.selectedPeriod,
                isLoadingCandles = state.isLoadingCandles,
                candlesError = state.candlesErrorMessage,
                onSelectPeriod = viewModel::selectPeriod,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun AssetDetailContent(
    asset: AssetResponse,
    candles: List<CandleResponse>,
    selectedPeriod: CandlePeriod,
    isLoadingCandles: Boolean,
    candlesError: String?,
    onSelectPeriod: (CandlePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AssetIdentity(asset = asset, candles = candles)

        ChartSection(
            candles = candles,
            selectedPeriod = selectedPeriod,
            isLoadingCandles = isLoadingCandles,
            candlesError = candlesError,
            onSelectPeriod = onSelectPeriod
        )

        InfoCard(asset = asset)

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun AssetIdentity(asset: AssetResponse, candles: List<CandleResponse>) {
    val periodPnlPercent: BigDecimal? = if (candles.size >= 2) {
        val first = candles.first().close
        val last = candles.last().close
        if (first.signum() == 0) null
        else last.subtract(first)
            .multiply(BigDecimal.fromInt(100))
            .divide(first, DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, scale = 4))
    } else null

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssetAvatar(ticker = asset.ticker, size = 48.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.ticker,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${asset.name} • ${asset.type.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = asset.lastPrice.formatMoney(),
                style = MoneyHero,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (periodPnlPercent != null) {
                    PnLChip(value = periodPnlPercent, isPercent = true)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                asset.lastPriceUpdatedAt?.let { instant ->
                    Text(
                        text = freshness(instant),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartSection(
    candles: List<CandleResponse>,
    selectedPeriod: CandlePeriod,
    isLoadingCandles: Boolean,
    candlesError: String?,
    onSelectPeriod: (CandlePeriod) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                candlesError != null && candles.isEmpty() -> ErrorBanner(message = candlesError)

                candles.isEmpty() && isLoadingCandles -> CircularProgressIndicator(strokeWidth = 2.dp)

                candles.isEmpty() -> Text(
                    text = "Pas de données",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                else -> PriceChart(prices = candles.map { it.close.floatValue(exactRequired = false) })
            }
        }

        SegmentedPeriodSelector(selected = selectedPeriod, onSelect = onSelectPeriod)
    }
}

@Composable
private fun SegmentedPeriodSelector(
    selected: CandlePeriod,
    onSelect: (CandlePeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        CandlePeriod.values().forEach { period ->
            val isSelected = period == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onSelect(period) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun InfoCard(asset: AssetResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Détails",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            InfoLine("Marché", asset.market)
            InfoLine("Type", asset.type.name)
            InfoLine("Identifiant", asset.id.toString().take(8) + "…")
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StickyBuyBar(ticker: String, onBuy: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        OrderButton(
            text = "Acheter $ticker",
            onClick = onBuy,
            style = OrderButtonStyle.Buy,
            leadingIcon = Icons.Outlined.ShoppingCart
        )
    }
}

private fun freshness(instant: Instant): String {
    val seconds = (Clock.System.now() - instant).inWholeSeconds
    return when {
        seconds < 60 -> "il y a ${seconds}s"
        seconds < 3600 -> "il y a ${seconds / 60} min"
        seconds < 86_400 -> "il y a ${seconds / 3600} h"
        else -> "il y a ${seconds / 86_400} j"
    }
}
