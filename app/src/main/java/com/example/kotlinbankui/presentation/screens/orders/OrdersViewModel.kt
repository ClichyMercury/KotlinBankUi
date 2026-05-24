package com.example.kotlinbankui.presentation.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.market.MarketRepository
import com.example.kotlinbankui.data.orders.OrderRepository
import com.example.kotlinbankui.presentation.screens.dashboard.uiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState(isLoading = true))
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    private fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val ordersDeferred = async { orderRepository.listOrders() }
            val assetsDeferred = async { marketRepository.listAssets() }
            val orders = ordersDeferred.await()
            val assets = assetsDeferred.await()

            val maybeError = orders.exceptionOrNull() ?: assets.exceptionOrNull()
            if (maybeError != null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = maybeError.uiMessage()) }
                return@launch
            }
            val lookup = assets.getOrNull().orEmpty().associate { it.id to it.ticker }
            _uiState.update {
                it.copy(
                    orders = orders.getOrNull().orEmpty().sortedByDescending { o -> o.createdAt },
                    tickerByAssetId = lookup,
                    isLoading = false
                )
            }
        }
    }
}
