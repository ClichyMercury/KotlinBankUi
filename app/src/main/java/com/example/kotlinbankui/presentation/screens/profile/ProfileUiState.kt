package com.example.kotlinbankui.presentation.screens.profile

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val quickStats: QuickStats = QuickStats(),
    val isLoading: Boolean = false,
    val error: String? = null
)