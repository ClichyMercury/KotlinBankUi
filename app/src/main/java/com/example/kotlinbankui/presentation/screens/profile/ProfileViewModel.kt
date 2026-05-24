package com.example.kotlinbankui.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.auth.AuthRepository
import com.example.kotlinbankui.data.auth.ThemePreference
import com.example.kotlinbankui.data.auth.ThemePreferenceStore
import com.example.kotlinbankui.presentation.screens.dashboard.uiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val themePreferenceStore: ThemePreferenceStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            themePreferenceStore.preference.collect { pref ->
                _uiState.update { it.copy(themePreference = pref) }
            }
        }
    }

    fun refresh() = load()

    private fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.me()
                .onSuccess { u -> _uiState.update { it.copy(user = u, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.uiMessage()) } }
        }
    }

    fun setThemePreference(preference: ThemePreference) {
        viewModelScope.launch { themePreferenceStore.set(preference) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }
}
