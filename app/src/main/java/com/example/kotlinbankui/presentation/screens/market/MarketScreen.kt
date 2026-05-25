package com.example.kotlinbankui.presentation.screens.market

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.AssetType
import com.example.kotlinbankui.presentation.components.finsim.AssetAvatar
import com.example.kotlinbankui.presentation.components.finsim.EmptyState
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimBottomBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.Sparkline
import com.example.kotlinbankui.presentation.components.finsim.formatMoney
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.example.kotlinbankui.ui.theme.MoneyText as MoneyTextStyle
import kotlinx.coroutines.delay

@Composable
fun MarketScreen(
    navController: NavController,
    viewModel: MarketViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                viewModel.refresh()
                delay(30_000L)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { FinSimBottomBar(navController) }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(modifier = Modifier.padding(padding))

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Marché",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 32.dp, bottom = 4.dp)
                    )
                }

                state.errorMessage?.let {
                    item { ErrorBanner(message = it, onRetry = viewModel::refresh) }
                }

                item { TypeFilterRow(selected = state.selectedType, onSelect = viewModel::selectType) }

                if (state.assets.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Outlined.QueryStats,
                            title = "Aucun actif",
                            message = "Essaie un autre filtre."
                        )
                    }
                } else {
                    items(state.assets, key = { it.id }) { asset ->
                        MarketAssetRow(
                            asset = asset,
                            sparkline = state.sparklines[asset.id],
                            onClick = { navController.navigate(NavigationRoutes.assetDetail(asset.id.toString())) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketAssetRow(
    asset: AssetResponse,
    sparkline: List<Float>?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssetAvatar(ticker = asset.ticker, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.ticker,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (sparkline != null && sparkline.size >= 2) {
                Sparkline(
                    values = sparkline,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(width = 64.dp, height = 32.dp)
                )
            }
            Text(
                text = asset.lastPrice.formatMoney(),
                style = MoneyTextStyle,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TypeFilterRow(
    selected: AssetType?,
    onSelect: (AssetType?) -> Unit
) {
    val options = listOf<Pair<String, AssetType?>>(
        "Tous" to null,
        "Crypto" to AssetType.CRYPTO,
        "Actions" to AssetType.STOCK,
        "Forex" to AssetType.FOREX
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(options) { (label, type) ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

