package com.example.kotlinbankui.presentation.screens.profile

import com.example.kotlinbankui.data.auth.ThemePreference
import com.finsim.data.network.dto.UserResponse

data class ProfileUiState(
    val user: UserResponse? = null,
    val themePreference: ThemePreference = ThemePreference.System,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedOut: Boolean = false
)
