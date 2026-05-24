package com.example.kotlinbankui.presentation.screens.trading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.market.MarketRepository
import com.example.kotlinbankui.data.orders.OrderRepository
import com.example.kotlinbankui.presentation.screens.dashboard.uiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BuyViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyUiState())
    val uiState: StateFlow<BuyUiState> = _uiState.asStateFlow()

    fun load(assetId: String) {
        val uuid = runCatching { UUID.fromString(assetId) }.getOrNull()
        if (uuid == null) {
            _uiState.update { it.copy(errorMessage = "Identifiant invalide") }
            return
        }
        _uiState.update { it.copy(isLoadingAsset = true, errorMessage = null) }
        viewModelScope.launch {
            marketRepository.getAsset(uuid)
                .onSuccess { a -> _uiState.update { it.copy(asset = a, isLoadingAsset = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoadingAsset = false, errorMessage = e.uiMessage()) } }
        }
    }

    fun onQuantityChange(text: String) {
        _uiState.update { it.copy(quantityText = text, errorMessage = null) }
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
