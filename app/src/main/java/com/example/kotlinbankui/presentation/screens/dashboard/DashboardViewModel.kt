package com.example.kotlinbankui.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.network.ApiException
import com.example.kotlinbankui.data.portfolio.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load(initial = true)
    }

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
            portfolioRepository.getPortfolio()
                .onSuccess { p ->
                    _uiState.update { it.copy(portfolio = p, isLoading = false, isRefreshing = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.uiMessage()) }
                }
        }
    }
}

internal fun Throwable.uiMessage(): String = when (this) {
    is ApiException.Http -> userMessage
    is ApiException.Unauthorized -> "Session expirée"
    is ApiException.Network -> "Pas de connexion"
    else -> message ?: "Erreur inconnue"
}
