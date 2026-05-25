package com.example.kotlinbankui.presentation.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.data.market.MarketRepository
import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.AssetType
import com.example.kotlinbankui.presentation.screens.dashboard.uiMessage
import com.finsim.presentation.screens.market.MarketUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState(isLoading = true))
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init { load(initial = true) }

    fun selectType(type: AssetType?) {
        _uiState.update { it.copy(selectedType = type) }
        load(initial = false)
    }

    fun refresh() = load(initial = false)

    private fun load(initial: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = initial && it.assets.isEmpty(),
                isRefreshing = !initial,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            marketRepository.listAssets(_uiState.value.selectedType)
                .onSuccess { list ->
                    _uiState.update { it.copy(assets = list, isLoading = false, isRefreshing = false) }
                    loadSparklines(list)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.uiMessage()) }
                }
        }
    }

    private fun loadSparklines(assets: List<AssetResponse>) {
        if (assets.isEmpty()) return
        viewModelScope.launch {
            val results = assets.map { asset ->
                async {
                    asset.id to marketRepository.getCandles(asset.id, days = 7)
                        .getOrNull()
                        ?.map { it.close.floatValue(exactRequired = false) }
                }
            }.map { it.await() }
            val map = results.mapNotNull { (id, prices) -> prices?.let { id to it } }.toMap()
            _uiState.update { it.copy(sparklines = it.sparklines + map) }
        }
    }
}
