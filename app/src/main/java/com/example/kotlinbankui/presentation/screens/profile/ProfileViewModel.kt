package com.example.kotlinbankui.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



data class QuickStats(
    val activeCards: Int = 3,
    val accounts: Int = 2,
    val totalTransactions: Int = 127
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Simuler le chargement des données
                val userProfile = UserProfile(
                    name = "John Doe",
                    email = "john.doe@email.com",
                    phone = "+1 (555) 123-4567",
                    accountNumber = "****5154",
                    memberSince = "March 2020",
                    accountType = "Premium Account"
                )

                val quickStats = QuickStats(
                    activeCards = 3,
                    accounts = 2,
                    totalTransactions = 127
                )

                _uiState.value = _uiState.value.copy(
                    userProfile = userProfile,
                    quickStats = quickStats,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun toggleNotifications() {
        _uiState.value = _uiState.value.copy(
            notificationsEnabled = !_uiState.value.notificationsEnabled
        )
        // TODO: Persister la préférence
    }

    fun toggleBiometric() {
        _uiState.value = _uiState.value.copy(
            biometricEnabled = !_uiState.value.biometricEnabled
        )
        // TODO: Configurer l'authentification biométrique
    }

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(
            darkModeEnabled = !_uiState.value.darkModeEnabled
        )
        // TODO: Appliquer le thème sombre
    }

    fun updateProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
            try {
                // TODO: Appel API pour mettre à jour le profil
                _uiState.value = _uiState.value.copy(
                    userProfile = updatedProfile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // TODO: Logique de déconnexion
                // Effacer les données locales, tokens, etc.
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun refreshProfile() {
        loadProfileData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}