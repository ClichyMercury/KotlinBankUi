package com.finsim.presentation.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finsim.data.network.dto.OrderResponse
import com.finsim.data.network.dto.OrderStatus
import com.finsim.data.network.dto.OrderType
import com.finsim.presentation.components.AssetAvatar
import com.finsim.presentation.components.EmptyState
import com.finsim.presentation.components.ErrorBanner
import com.finsim.presentation.components.LoadingScreen
import com.finsim.presentation.components.formatMoney
import com.finsim.presentation.components.formatQuantity
import com.finsim.ui.theme.TrendDown
import com.finsim.ui.theme.TrendUp
import com.finsim.ui.theme.MoneyText as MoneyTextStyle
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Composable
fun OrdersScreen(
    state: OrdersUiState,
    onRefresh: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = bottomBar
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(Modifier.padding(padding))

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Mes ordres",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 32.dp, bottom = 4.dp)
                    )
                }

                state.errorMessage?.let {
                    item { ErrorBanner(message = it, onRetry = onRefresh) }
                }

                if (state.orders.isEmpty() && state.errorMessage == null) {
                    item {
                        EmptyState(
                            icon = Icons.Outlined.Inbox,
                            title = "Aucun ordre",
                            message = "Tes achats apparaîtront ici."
                        )
                    }
                } else {
                    val grouped = state.orders.groupBy { groupForDate(it.createdAt) }
                    val orderedGroups = listOf("Aujourd'hui", "Hier", "Cette semaine", "Plus ancien")
                        .filter { grouped.containsKey(it) }

                    orderedGroups.forEach { groupLabel ->
                        item {
                            Text(
                                text = groupLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(grouped[groupLabel].orEmpty(), key = { it.id.toString() }) { order ->
                            OrderCard(
                                order = order,
                                ticker = state.tickerByAssetId[order.assetId] ?: order.assetId.toString().take(6)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderResponse, ticker: String) {
    val typeColor = if (order.type == OrderType.BUY) TrendUp else TrendDown
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssetAvatar(ticker = ticker, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticker,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TypeTag(text = order.type.name, color = typeColor)
                }
                Spacer(modifier = Modifier.padding(top = 2.dp))
                Text(
                    text = "${order.quantity.formatQuantity()} × ${order.price.formatMoney()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatTime(order.createdAt)} • ${statusLabel(order.status)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = order.total.formatMoney(), style = MoneyTextStyle, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                StatusTag(status = order.status)
            }
        }
    }
}

@Composable
private fun TypeTag(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun StatusTag(status: OrderStatus) {
    val (text, color) = when (status) {
        OrderStatus.EXECUTED -> "Exécuté" to TrendUp
        OrderStatus.PENDING -> "En attente" to MaterialTheme.colorScheme.tertiary
        OrderStatus.FAILED -> "Échoué" to TrendDown
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

private fun statusLabel(status: OrderStatus): String = when (status) {
    OrderStatus.EXECUTED -> "exécuté"
    OrderStatus.PENDING -> "en attente"
    OrderStatus.FAILED -> "échoué"
}

private val timeFormat = LocalTime.Format {
    hour()
    char(':')
    minute()
}

private fun formatTime(instant: Instant): String {
    val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return timeFormat.format(ldt.time)
}

private fun groupForDate(instant: Instant): String {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date
    val date = instant.toLocalDateTime(tz).date
    val days = date.daysUntil(today)
    return when {
        days == 0 -> "Aujourd'hui"
        days == 1 -> "Hier"
        days in 2..7 -> "Cette semaine"
        else -> "Plus ancien"
    }
}
