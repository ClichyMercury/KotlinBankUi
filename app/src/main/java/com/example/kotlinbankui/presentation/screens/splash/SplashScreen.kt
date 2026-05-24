package com.example.kotlinbankui.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlinbankui.ui.theme.FinSimBlue
import com.example.kotlinbankui.ui.theme.FinSimBlueDark

@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home -> onAuthenticated()
            SplashDestination.Login -> onUnauthenticated()
            SplashDestination.Loading -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(FinSimBlue, FinSimBlueDark))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FinSim",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            Text(
                text = "Trading simulé, vrai apprentissage",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}
