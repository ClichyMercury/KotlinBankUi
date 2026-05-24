package com.example.kotlinbankui.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.auth.AuthRepository
import com.example.kotlinbankui.data.network.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value.trim(), errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.login(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, loggedIn = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
                }
        }
    }
}

internal fun Throwable.userMessage(): String = when (this) {
    is ApiException.Http -> userMessage
    is ApiException.Unauthorized -> "Email ou mot de passe incorrect"
    is ApiException.Network -> "Pas de connexion au serveur"
    else -> message ?: "Erreur inconnue"
}
