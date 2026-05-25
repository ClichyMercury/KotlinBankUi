package com.finsim.presentation.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.finsim.resources.Res
import com.finsim.resources.logo_landscape
import com.finsim.ui.theme.BrandGradientEnd
import com.finsim.ui.theme.BrandGradientStart
import com.finsim.ui.theme.NightGradientEnd
import com.finsim.ui.theme.NightGradientStart
import org.jetbrains.compose.resources.painterResource

enum class SplashDestination { Loading, Home, Login }

@Composable
fun SplashScreen(
    destination: SplashDestination,
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit
) {
    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home -> onAuthenticated()
            SplashDestination.Login -> onUnauthenticated()
            SplashDestination.Loading -> Unit
        }
    }

    val dark = isSystemInDarkTheme()
    val gradient = if (dark) {
        Brush.verticalGradient(listOf(NightGradientStart, NightGradientEnd))
    } else {
        Brush.verticalGradient(listOf(BrandGradientStart, BrandGradientEnd))
    }

    val infinite = rememberInfiniteTransition(label = "splashPulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_landscape),
                contentDescription = "FinSim",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Trading simulé, vrai apprentissage",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.5.dp,
                modifier = Modifier
                    .size(28.dp)
                    .alpha(pulse)
            )
        }
    }
}
