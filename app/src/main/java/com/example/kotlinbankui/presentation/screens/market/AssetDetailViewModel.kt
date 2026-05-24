package com.example.kotlinbankui.presentation.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.market.MarketRepository
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
class AssetDetailViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetDetailUiState(isLoading = true))
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    fun load(assetId: String) {
        val uuid = runCatching { UUID.fromString(assetId) }.getOrNull()
        if (uuid == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Identifiant invalide") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            marketRepository.getAsset(uuid)
                .onSuccess { a -> _uiState.update { it.copy(asset = a, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.uiMessage()) } }
        }
    }
}
