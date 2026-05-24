package com.example.kotlinbankui.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.kotlinbankui.data.network.dto.PortfolioAssetResponse
import com.example.kotlinbankui.data.network.dto.PortfolioResponse
import com.example.kotlinbankui.data.network.dto.UserResponse
import com.example.kotlinbankui.presentation.components.finsim.AssetAvatar
import com.example.kotlinbankui.presentation.components.finsim.EmptyState
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimBottomBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.PnLChip
import com.example.kotlinbankui.presentation.components.finsim.Sparkline
import com.example.kotlinbankui.presentation.components.finsim.computePnlPercent
import com.example.kotlinbankui.presentation.components.finsim.formatMoney
import com.example.kotlinbankui.presentation.components.finsim.formatQuantity
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.example.kotlinbankui.ui.theme.BrandGradientEnd
import com.example.kotlinbankui.ui.theme.BrandGradientStart
import com.example.kotlinbankui.ui.theme.MoneyHero
import com.example.kotlinbankui.ui.theme.MoneyText as MoneyTextStyle
import com.example.kotlinbankui.ui.theme.NightGradientEnd
import com.example.kotlinbankui.ui.theme.NightGradientStart
import java.math.BigDecimal
import java.util.UUID

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
        containerColor = MaterialTheme.colorScheme.background,
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
                ErrorBanner(
                    message = state.errorMessage ?: "Impossible de charger le portfolio",
                    onRetry = viewModel::refresh
                )
            }

            else -> DashboardContent(
                portfolio = state.portfolio!!,
                user = state.user,
                sparklines = state.sparklines,
                errorMessage = state.errorMessage,
                contentPadding = padding,
                onRetry = viewModel::refresh,
                onGoToMarket = { navController.navigate(NavigationRoutes.MARKET) },
                onGoToOrders = { navController.navigate(NavigationRoutes.ORDERS) },
                onAssetClick = { id -> navController.navigate(NavigationRoutes.assetDetail(id)) }
            )
        }
    }
}

@Composable
private fun DashboardContent(
    portfolio: PortfolioResponse,
    user: UserResponse?,
    sparklines: Map<UUID, List<Float>>,
    errorMessage: String?,
    contentPadding: PaddingValues,
    onRetry: () -> Unit,
    onGoToMarket: () -> Unit,
    onGoToOrders: () -> Unit,
    onAssetClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                GreetingRow(user = user, onRefresh = onRetry)
            }
        }

        errorMessage?.let {
            item { ErrorBanner(message = it, onRetry = onRetry) }
        }

        item { HeroPortfolioCard(portfolio = portfolio) }

        item { SplitStats(portfolio = portfolio) }

        item {
            QuickActionsRow(
                onTrade = onGoToMarket,
                onHistory = onGoToOrders,
                onBuy = onGoToMarket
            )
        }

        item {
            Text(
                text = "Mes positions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (portfolio.assets.isEmpty()) {
            item { EmptyPositionsCard(onGoToMarket = onGoToMarket) }
        } else {
            items(portfolio.assets, key = { it.assetId }) { asset ->
                PositionRow(
                    asset = asset,
                    sparkline = sparklines[asset.assetId],
                    onClick = { onAssetClick(asset.assetId.toString()) }
                )
            }
        }
    }
}

@Composable
private fun GreetingRow(user: UserResponse?, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (user != null) "Hello, @${user.pseudo}" else "Hello",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Portfolio",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold
            )
        }
        IconButton(
            onClick = onRefresh,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Rafraîchir",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun HeroPortfolioCard(portfolio: PortfolioResponse) {
    val dark = isSystemInDarkTheme()
    val gradient = if (dark) {
        Brush.linearGradient(listOf(NightGradientEnd, NightGradientStart))
    } else {
        Brush.linearGradient(listOf(BrandGradientStart, BrandGradientEnd))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient, shape = RoundedCornerShape(28.dp))
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "VALEUR TOTALE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = portfolio.totalValue.formatMoney(),
                    style = MoneyHero,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Simulation pédagogique — argent fictif",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.55f)
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
            label = "Investi",
            value = portfolio.assetsValue.formatMoney(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
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
                text = label.uppercase(),
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
private fun QuickActionsRow(
    onTrade: () -> Unit,
    onHistory: () -> Unit,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAction(label = "Trader", icon = Icons.Outlined.ShoppingCart, onClick = onTrade, modifier = Modifier.weight(1f))
        QuickAction(label = "Acheter", icon = Icons.Outlined.Add, onClick = onBuy, modifier = Modifier.weight(1f))
        QuickAction(label = "Historique", icon = Icons.Outlined.History, onClick = onHistory, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PositionRow(
    asset: PortfolioAssetResponse,
    sparkline: List<Float>?,
    onClick: () -> Unit
) {
    val costBasis: BigDecimal = asset.avgBuyPrice.multiply(asset.quantity)
    val pnlPercent = computePnlPercent(asset.unrealizedPnl, costBasis)

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
                    text = "${asset.quantity.formatQuantity()} • PRU ${asset.avgBuyPrice.formatMoney()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (sparkline != null && sparkline.size >= 2) {
                Sparkline(
                    values = sparkline,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(width = 64.dp, height = 32.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = asset.currentValue.formatMoney(),
                    style = MoneyTextStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                PnLChip(value = pnlPercent, isPercent = true)
            }
        }
    }
}

@Composable
private fun EmptyPositionsCard(onGoToMarket: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
            EmptyState(
                icon = Icons.Outlined.AccountBalanceWallet,
                title = "Aucune position",
                message = "Tu n'as encore rien acheté. Direction le marché pour passer ton premier ordre."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
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
    }
}
