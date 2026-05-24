package com.example.kotlinbankui.presentation.screens.profile

import com.example.kotlinbankui.data.network.dto.UserResponse

data class ProfileUiState(
    val user: UserResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedOut: Boolean = false
)
