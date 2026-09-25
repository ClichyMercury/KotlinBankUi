package com.finsim.presentation.screens.auth

enum class PasswordResetStep { RequestCode, EnterCode, Done }

data class PasswordResetUiState(
    val step: PasswordResetStep = PasswordResetStep.RequestCode,
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
    val canRequestCode: Boolean
        get() = !isLoading && email.isNotBlank()

    val canSubmitNewPassword: Boolean
        get() = !isLoading && code.isNotBlank() && newPassword.length >= 8
}
