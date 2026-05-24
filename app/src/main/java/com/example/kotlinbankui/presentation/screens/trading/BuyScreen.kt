package com.example.kotlinbankui.presentation.screens.trading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.finsim.AssetAvatar
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimTextField
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.LoadingScreen
import com.example.kotlinbankui.presentation.components.finsim.OrderButton
import com.example.kotlinbankui.presentation.components.finsim.OrderButtonStyle
import com.example.kotlinbankui.presentation.components.finsim.formatMoney
import com.example.kotlinbankui.ui.theme.MoneyHero
import com.example.kotlinbankui.ui.theme.MoneyText as MoneyTextStyle

@Composable
fun BuyScreen(
    navController: NavController,
    assetId: String,
    viewModel: BuyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinSimTopBar(
                title = "Acheter",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        when {
            state.isLoadingAsset && state.asset == null -> LoadingScreen(Modifier.padding(padding))

            state.orderPlaced != null -> SuccessContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                onDone = { navController.popBackStack() }
            )

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

            else -> BuyForm(
                state = state,
                onQuantityChange = viewModel::onQuantityChange,
                onPreset = viewModel::applyPercent,
                onSubmit = viewModel::submit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun BuyForm(
    state: BuyUiState,
    onQuantityChange: (String) -> Unit,
    onPreset: (Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val asset = state.asset!!
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Asset header
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssetAvatar(ticker = asset.ticker, size = 48.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(asset.ticker, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(asset.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Prix unitaire", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(asset.lastPrice.formatMoney(), style = MoneyTextStyle)
            }
        }

        // Available cash
        state.availableCash?.let { cash ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cash disponible", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(cash.formatMoney(), style = MoneyTextStyle, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            }
        }

        // Quantity field
        FinSimTextField(
            value = state.quantityText,
            onValueChange = onQuantityChange,
            label = "Quantité de ${asset.ticker}",
            placeholder = "Ex. 0.01",
            keyboardType = KeyboardType.Decimal,
            enabled = !state.isSubmitting
        )

        // Quick presets
        if (state.availableCash != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(25, 50, 75, 100).forEach { pct ->
                    PresetChip(
                        label = if (pct == 100) "Max" else "$pct%",
                        onClick = { onPreset(pct) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Total card with big number
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "COÛT TOTAL ESTIMÉ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = state.total.formatMoney(),
                    style = MoneyHero,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        state.errorMessage?.let { ErrorBanner(message = it) }

        Spacer(modifier = Modifier.height(4.dp))

        OrderButton(
            text = "Confirmer l'achat",
            onClick = onSubmit,
            enabled = state.canSubmit,
            isLoading = state.isSubmitting,
            style = OrderButtonStyle.Buy,
            leadingIcon = Icons.Outlined.ShoppingCart
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SuccessContent(modifier: Modifier = Modifier, onDone: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(72.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ordre exécuté",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ton ordre a été pris en compte et ton portfolio est mis à jour.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        OrderButton(text = "Retour", onClick = onDone, style = OrderButtonStyle.Primary)
    }
}
