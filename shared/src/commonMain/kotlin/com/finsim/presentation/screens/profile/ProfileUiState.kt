package com.finsim.presentation.screens.profile

import com.finsim.data.network.dto.UserResponse
import com.finsim.ui.theme.ThemePreference

data class ProfileUiState(
    val user: UserResponse? = null,
    val themePreference: ThemePreference = ThemePreference.System,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedOut: Boolean = false
)
