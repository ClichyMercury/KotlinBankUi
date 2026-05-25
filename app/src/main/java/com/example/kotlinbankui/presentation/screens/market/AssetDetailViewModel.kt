package com.example.kotlinbankui.presentation.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.data.market.MarketRepository
import com.example.kotlinbankui.presentation.screens.dashboard.uiMessage
import com.finsim.presentation.screens.market.AssetDetailUiState
import com.finsim.presentation.screens.market.CandlePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@HiltViewModel
class AssetDetailViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetDetailUiState(isLoading = true))
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    private var currentAssetId: Uuid? = null

    fun load(assetId: String) {
        val uuid = runCatching { Uuid.parse(assetId) }.getOrNull()
        if (uuid == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Identifiant invalide") }
            return
        }
        currentAssetId = uuid
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            marketRepository.getAsset(uuid)
                .onSuccess { a -> _uiState.update { it.copy(asset = a, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.uiMessage()) } }
        }
        loadCandles(uuid, _uiState.value.selectedPeriod)
    }

    fun selectPeriod(period: CandlePeriod) {
        if (_uiState.value.selectedPeriod == period && _uiState.value.candles.isNotEmpty()) return
        _uiState.update { it.copy(selectedPeriod = period) }
        currentAssetId?.let { loadCandles(it, period) }
    }

    private fun loadCandles(assetId: Uuid, period: CandlePeriod) {
        _uiState.update { it.copy(isLoadingCandles = true, candlesErrorMessage = null) }
        viewModelScope.launch {
            marketRepository.getCandles(assetId, period.days)
                .onSuccess { list ->
                    _uiState.update { it.copy(candles = list, isLoadingCandles = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingCandles = false, candlesErrorMessage = e.uiMessage()) }
                }
        }
    }
}
