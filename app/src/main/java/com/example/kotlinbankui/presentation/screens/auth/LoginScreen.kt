package com.example.kotlinbankui.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimTextField
import com.example.kotlinbankui.presentation.components.finsim.OrderButton

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) onLoggedIn()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "FinSim",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bon retour",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Connecte-toi pour reprendre ton simulateur",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            FinSimTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "Email",
                placeholder = "ton@email.com",
                enabled = !state.isLoading,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            FinSimTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Mot de passe",
                enabled = !state.isLoading,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Cacher" else "Voir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            state.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                ErrorBanner(message = msg)
            }

            Spacer(modifier = Modifier.height(28.dp))

            OrderButton(
                text = "Se connecter",
                onClick = viewModel::submit,
                enabled = state.canSubmit,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onGoToRegister,
                enabled = !state.isLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Pas de compte ? Créer un compte",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
