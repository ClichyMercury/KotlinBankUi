package com.finsim.presentation.screens.trading

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
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finsim.presentation.components.AssetAvatar
import com.finsim.presentation.components.ErrorBanner
import com.finsim.presentation.components.FinSimTextField
import com.finsim.presentation.components.FinSimTopBar
import com.finsim.presentation.components.LoadingScreen
import com.finsim.presentation.components.OrderButton
import com.finsim.presentation.components.OrderButtonStyle
import com.finsim.presentation.components.formatMoney
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.finsim.ui.theme.MoneyHero
import com.finsim.ui.theme.MoneyText as MoneyTextStyle

@Composable
fun SellScreen(
    state: SellUiState,
    onBack: () -> Unit,
    onQuantityChange: (String) -> Unit,
    onPreset: (Int) -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onDone: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinSimTopBar(title = "Vendre", onBack = onBack)
        }
    ) { padding ->
        when {
            state.isLoadingAsset && state.asset == null -> LoadingScreen(Modifier.padding(padding))

            state.orderPlaced != null -> SuccessContent(
                realizedPnl = state.orderPlaced.realizedPnl,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                onDone = onDone
            )

            state.asset == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                ErrorBanner(
                    message = state.errorMessage ?: "Actif introuvable",
                    onRetry = onRetry
                )
            }

            else -> SellForm(
                state = state,
                onQuantityChange = onQuantityChange,
                onPreset = onPreset,
                onSubmit = onSubmit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun SellForm(
    state: SellUiState,
    onQuantityChange: (String) -> Unit,
    onPreset: (Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val asset = state.asset!!
    val hasPosition = state.heldQuantity?.signum()?.let { it > 0 } == true

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Position détenue", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = state.heldQuantity?.let { "${it.toPlainString()} ${asset.ticker}" } ?: "—",
                style = MoneyTextStyle,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }

        FinSimTextField(
            value = state.quantityText,
            onValueChange = onQuantityChange,
            label = "Quantité à vendre",
            placeholder = "Ex. 0.01",
            keyboardType = KeyboardType.Decimal,
            enabled = !state.isSubmitting && hasPosition
        )

        if (hasPosition) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(25, 50, 75, 100).forEach { pct ->
                    PresetChip(
                        label = if (pct == 100) "Tout" else "$pct%",
                        onClick = { onPreset(pct) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

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
                    text = "MONTANT REÇU ESTIMÉ",
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

        val inlineError = when {
            !hasPosition -> "Tu ne détiens pas ${asset.ticker}"
            state.exceedsHeld -> "Quantité supérieure à ta position"
            else -> state.errorMessage
        }
        inlineError?.let { ErrorBanner(message = it) }

        Spacer(modifier = Modifier.height(4.dp))

        OrderButton(
            text = "Confirmer la vente",
            onClick = onSubmit,
            enabled = state.canSubmit,
            isLoading = state.isSubmitting,
            style = OrderButtonStyle.Sell,
            leadingIcon = Icons.Outlined.Sell
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
private fun SuccessContent(
    realizedPnl: BigDecimal?,
    modifier: Modifier = Modifier,
    onDone: () -> Unit
) {
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
            text = "Vente exécutée",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        if (realizedPnl != null) {
            Spacer(modifier = Modifier.height(16.dp))
            val isGain = realizedPnl.signum() >= 0
            val color = if (isGain) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
            val prefix = if (isGain) "+" else ""
            Text(
                text = "$prefix${realizedPnl.formatMoney()} réalisés",
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ton solde et ton portfolio sont mis à jour.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        OrderButton(text = "Retour", onClick = onDone, style = OrderButtonStyle.Primary)
    }
}
