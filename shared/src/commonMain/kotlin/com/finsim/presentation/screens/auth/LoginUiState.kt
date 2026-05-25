package com.finsim.presentation.screens.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedIn: Boolean = false
) {
    val canSubmit: Boolean
        get() = !isLoading && email.isNotBlank() && password.length >= 8
}
