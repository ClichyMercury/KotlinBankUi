package com.example.kotlinbankui.presentation.screens.wallet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class WalletViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        // Pour l'instant, on utilise des données mockées
        // Plus tard, on ajoutera les vrais appels API
    }

    fun refreshData() {
        loadWalletData()
    }
}