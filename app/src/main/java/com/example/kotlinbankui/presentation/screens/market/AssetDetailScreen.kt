package com.example.kotlinbankui.presentation.screens.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kotlinbankui.data.network.dto.AssetResponse
import com.example.kotlinbankui.presentation.components.finsim.AssetAvatar
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.OrderButton
import com.example.kotlinbankui.presentation.components.finsim.OrderButtonStyle
import com.example.kotlinbankui.presentation.components.finsim.formatMoney
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.example.kotlinbankui.ui.theme.MoneyLarge
import java.time.Duration
import java.time.Instant

@Composable
fun AssetDetailScreen(
    navController: NavController,
    assetId: String,
    viewModel: AssetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    Scaffold(
        topBar = {
            FinSimTopBar(
                title = state.asset?.ticker ?: "Actif",
                onBack = { navController.popBackStack() }
            )
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
                onBuy = { navController.navigate(NavigationRoutes.buy(assetId)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun AssetDetailContent(
    asset: AssetResponse,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssetAvatar(ticker = asset.ticker, size = 56.dp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(asset.ticker, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LabelBadge(text = asset.type.name)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Dernier prix",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = asset.lastPrice.formatMoney(),
                        style = MoneyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    asset.lastPriceUpdatedAt?.let { instant ->
                        Text(
                            text = "Mis à jour ${freshness(instant)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoLine("Marché", asset.market)
                InfoLine("Type", asset.type.name)
                InfoLine("Identifiant", asset.id.toString().take(8) + "…")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OrderButton(
            text = "Acheter ${asset.ticker}",
            onClick = onBuy,
            style = OrderButtonStyle.Buy,
            leadingIcon = Icons.Outlined.ShoppingCart
        )
    }
}

@Composable
private fun LabelBadge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .padding(0.dp)
    )
    // Tag styling kept minimal — primary container background applied via Surface in caller if needed.
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun freshness(instant: Instant): String {
    val delta = Duration.between(instant, Instant.now())
    val seconds = delta.seconds
    return when {
        seconds < 60 -> "il y a ${seconds}s"
        seconds < 3600 -> "il y a ${seconds / 60} min"
        seconds < 86_400 -> "il y a ${seconds / 3600} h"
        else -> "il y a ${seconds / 86_400} j"
    }
}
