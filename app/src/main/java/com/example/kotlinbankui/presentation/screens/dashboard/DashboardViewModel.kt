package com.example.kotlinbankui.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.auth.AuthRepository
import com.example.kotlinbankui.data.market.MarketRepository
import com.example.kotlinbankui.data.network.ApiException
import com.example.kotlinbankui.data.network.dto.PortfolioAssetResponse
import com.example.kotlinbankui.data.portfolio.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val authRepository: AuthRepository,
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { load(initial = true) }

    fun refresh() = load(initial = false)

    private fun load(initial: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = initial && it.portfolio == null,
                isRefreshing = !initial,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            val portfolioDeferred = async { portfolioRepository.getPortfolio() }
            val userDeferred = async { authRepository.me() }
            val portfolio = portfolioDeferred.await()
            val user = userDeferred.await()

            portfolio
                .onSuccess { p ->
                    _uiState.update { it.copy(
                        portfolio = p,
                        user = user.getOrNull() ?: it.user,
                        isLoading = false,
                        isRefreshing = false
                    ) }
                    loadSparklines(p.assets)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.uiMessage()
                    ) }
                }
        }
    }

    private fun loadSparklines(positions: List<PortfolioAssetResponse>) {
        if (positions.isEmpty()) return
        viewModelScope.launch {
            val results = positions.map { pos ->
                async {
                    pos.assetId to marketRepository.getCandles(pos.assetId, days = 7)
                        .getOrNull()
                        ?.map { it.close.toFloat() }
                }
            }.map { it.await() }
            val map = results.mapNotNull { (id, prices) -> prices?.let { id to it } }.toMap()
            _uiState.update { it.copy(sparklines = it.sparklines + map) }
        }
    }
}

internal fun Throwable.uiMessage(): String = when (this) {
    is ApiException.Http -> userMessage
    is ApiException.Unauthorized -> "Session expirée"
    is ApiException.Network -> "Pas de connexion"
    else -> message ?: "Erreur inconnue"
}
