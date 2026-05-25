package com.finsim.presentation.screens.auth

data class RegisterUiState(
    val email: String = "",
    val pseudo: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registered: Boolean = false
) {
    val canSubmit: Boolean
        get() = !isLoading &&
            email.isNotBlank() &&
            pseudo.length in 3..50 &&
            pseudo.matches(Regex("[a-zA-Z0-9_]+")) &&
            password.length >= 8
}
