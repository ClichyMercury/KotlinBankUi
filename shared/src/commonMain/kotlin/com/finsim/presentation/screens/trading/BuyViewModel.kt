package com.finsim.presentation.screens.trading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.data.market.MarketRepository
import com.finsim.data.orders.OrderRepository
import com.finsim.data.portfolio.PortfolioRepository
import com.finsim.presentation.util.uiMessage
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlin.uuid.Uuid
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuyViewModel(
    private val marketRepository: MarketRepository,
    private val orderRepository: OrderRepository,
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyUiState())
    val uiState: StateFlow<BuyUiState> = _uiState.asStateFlow()

    fun load(assetId: String) {
        val uuid = runCatching { Uuid.parse(assetId) }.getOrNull()
        if (uuid == null) {
            _uiState.update { it.copy(errorMessage = "Identifiant invalide") }
            return
        }
        _uiState.update { it.copy(isLoadingAsset = true, errorMessage = null) }
        viewModelScope.launch {
            val assetDeferred = async { marketRepository.getAsset(uuid) }
            val portfolioDeferred = async { portfolioRepository.getPortfolio() }
            val asset = assetDeferred.await()
            val portfolio = portfolioDeferred.await()

            asset
                .onSuccess { a ->
                    _uiState.update { it.copy(
                        asset = a,
                        availableCash = portfolio.getOrNull()?.balanceFictif,
                        isLoadingAsset = false
                    ) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingAsset = false, errorMessage = e.uiMessage()) }
                }
        }
    }

    fun onQuantityChange(text: String) {
        _uiState.update { it.copy(quantityText = text, errorMessage = null) }
    }

    fun applyPercent(percent: Int) {
        val state = _uiState.value
        val cash = state.availableCash ?: return
        val price = state.asset?.lastPrice ?: return
        if (price.signum() <= 0) return
        val budgetMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO, scale = 2)
        val qtyMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO, scale = 8)
        val budget = cash.multiply(BigDecimal.fromInt(percent)).divide(BigDecimal.fromInt(100), budgetMode)
        val qty = budget.divide(price, qtyMode)
        val cleanText = qty.toPlainString().let {
            if (it.contains('.')) it.trimEnd('0').trimEnd('.') else it
        }
        _uiState.update { it.copy(quantityText = cleanText, errorMessage = null) }
    }

    fun submit() {
        val state = _uiState.value
        val asset = state.asset ?: return
        val qty = state.quantity ?: return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            orderRepository.placeBuy(asset.id, qty)
                .onSuccess { o -> _uiState.update { it.copy(isSubmitting = false, orderPlaced = o) } }
                .onFailure { e -> _uiState.update { it.copy(isSubmitting = false, errorMessage = e.uiMessage()) } }
        }
    }
}
