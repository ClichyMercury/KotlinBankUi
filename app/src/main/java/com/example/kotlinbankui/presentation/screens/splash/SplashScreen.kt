package com.example.kotlinbankui.presentation.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.presentation.screens.splash.SplashScreen as SharedSplashScreen

@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()
    SharedSplashScreen(
        destination = destination,
        onAuthenticated = onAuthenticated,
        onUnauthenticated = onUnauthenticated
    )
}
