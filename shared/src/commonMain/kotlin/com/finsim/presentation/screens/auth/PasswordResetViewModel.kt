package com.finsim.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.data.auth.AuthRepository
import com.finsim.presentation.util.passwordResetMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val CODE_SENT =
    "Si cet email est enregistré, un code vient d'être envoyé. Regarde ta boîte mail."

class PasswordResetViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordResetUiState())
    val uiState: StateFlow<PasswordResetUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value.trim(), errorMessage = null) }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value.trim(), errorMessage = null) }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value, errorMessage = null) }
    }

    fun requestCode() {
        val state = _uiState.value
        if (!state.canRequestCode) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.forgotPassword(state.email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = PasswordResetStep.EnterCode,
                            infoMessage = CODE_SENT
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.passwordResetMessage()) }
                }
        }
    }

    fun submitNewPassword() {
        val state = _uiState.value
        if (!state.canSubmitNewPassword) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.resetPassword(state.code, state.newPassword)
                .onSuccess {
                    authRepository.logout()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = PasswordResetStep.Done,
                            code = "",
                            newPassword = "",
                            infoMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.passwordResetMessage()) }
                }
        }
    }

    fun backToRequestCode() {
        _uiState.update {
            it.copy(
                step = PasswordResetStep.RequestCode,
                code = "",
                newPassword = "",
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun goToEnterCode() {
        _uiState.update { it.copy(step = PasswordResetStep.EnterCode, errorMessage = null) }
    }
}
