package com.example.kotlinbankui.presentation.screens.dashboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.kotlinbankui.data.network.dto.PortfolioAssetResponse
import com.example.kotlinbankui.data.network.dto.PortfolioResponse
import com.example.kotlinbankui.presentation.components.finsim.AssetAvatar
import com.example.kotlinbankui.presentation.components.finsim.EmptyState
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimBottomBar
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.PnLChip
import com.example.kotlinbankui.presentation.components.finsim.computePnlPercent
import com.example.kotlinbankui.presentation.components.finsim.formatMoney
import com.example.kotlinbankui.presentation.components.finsim.formatQuantity
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.example.kotlinbankui.ui.theme.FinSimBlue
import com.example.kotlinbankui.ui.theme.FinSimBlueDark
import com.example.kotlinbankui.ui.theme.MoneyHuge
import com.example.kotlinbankui.ui.theme.MoneyText as MoneyTextStyle

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            FinSimTopBar(
                title = "Portfolio",
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir"
                        )
                    }
                }
            )
        },
        bottomBar = { FinSimBottomBar(navController) }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(modifier = Modifier.padding(padding))

            state.portfolio == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                state.errorMessage?.let { ErrorBanner(message = it, onRetry = viewModel::refresh) }
            }

            else -> DashboardContent(
                portfolio = state.portfolio!!,
                errorMessage = state.errorMessage,
                contentPadding = padding,
                onRetry = viewModel::refresh,
                onGoToMarket = { navController.navigate(NavigationRoutes.MARKET) },
                onAssetClick = { id -> navController.navigate(NavigationRoutes.assetDetail(id)) }
            )
        }
    }
}

@Composable
private fun DashboardContent(
    portfolio: PortfolioResponse,
    errorMessage: String?,
    contentPadding: PaddingValues,
    onRetry: () -> Unit,
    onGoToMarket: () -> Unit,
    onAssetClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        errorMessage?.let {
            item { ErrorBanner(message = it, onRetry = onRetry) }
        }

        item { HeroPortfolioCard(portfolio = portfolio) }

        item { SplitStats(portfolio = portfolio) }

        item {
            Text(
                text = "Mes positions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        if (portfolio.assets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    EmptyState(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Aucune position",
                        message = "Tu n'as encore rien acheté. Direction le marché pour passer ton premier ordre."
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onGoToMarket() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Explorer le marché",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        } else {
            items(portfolio.assets, key = { it.assetId }) { asset ->
                PositionRow(
                    asset = asset,
                    onClick = { onAssetClick(asset.assetId.toString()) }
                )
            }
        }
    }
}

@Composable
private fun HeroPortfolioCard(portfolio: PortfolioResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(FinSimBlue, FinSimBlueDark)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Valeur totale",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = portfolio.totalValue.formatMoney(),
                    color = Color.White,
                    style = MoneyHuge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Simulation pédagogique — argent fictif",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SplitStats(portfolio: PortfolioResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatTile(
            label = "Cash disponible",
            value = portfolio.balanceFictif.formatMoney(),
            modifier = Modifier.weight(1f)
        )
        StatTile(
            label = "Valeur des actifs",
            value = portfolio.assetsValue.formatMoney(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MoneyTextStyle,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PositionRow(
    asset: PortfolioAssetResponse,
    onClick: () -> Unit
) {
    val costBasis = asset.avgBuyPrice.multiply(asset.quantity)
    val pnlPercent = computePnlPercent(asset.unrealizedPnl, costBasis)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
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
                    text = "${asset.quantity.formatQuantity()} ${asset.ticker} • PRU ${asset.avgBuyPrice.formatMoney()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = asset.currentValue.formatMoney(),
                    style = MoneyTextStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PnLChip(value = pnlPercent, isPercent = true)
            }
        }
    }
}
