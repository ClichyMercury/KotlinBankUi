package com.example.kotlinbankui.presentation.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.market.MarketRepository
import com.example.kotlinbankui.data.network.dto.AssetType
import com.example.kotlinbankui.presentation.screens.dashboard.uiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.uiMessage()) }
                }
        }
    }
}
