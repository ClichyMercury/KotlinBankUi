package com.example.kotlinbankui.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinbankui.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination { Loading, Home, Login }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val token = authRepository.token.first()
            if (token.isNullOrBlank()) {
                _destination.value = SplashDestination.Login
                return@launch
            }
            authRepository.me()
                .onSuccess { _destination.value = SplashDestination.Home }
                .onFailure { _destination.value = SplashDestination.Login }
        }
    }
}
