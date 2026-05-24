package com.example.kotlinbankui.presentation.screens.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.kotlinbankui.data.network.dto.AssetType
import com.example.kotlinbankui.presentation.components.finsim.EmptyState
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimBottomBar
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.PriceCard
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes

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
        topBar = {
            FinSimTopBar(
                title = "Marché",
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
                    }
                }
            )
        },
        bottomBar = { FinSimBottomBar(navController) }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(modifier = Modifier.padding(padding))

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                        PriceCard(
                            ticker = asset.ticker,
                            name = asset.name,
                            price = asset.lastPrice,
                            subtitle = "${asset.name} • ${asset.type.name}",
                            onClick = { navController.navigate(NavigationRoutes.assetDetail(asset.id.toString())) }
                        )
                    }
                }
            }
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
