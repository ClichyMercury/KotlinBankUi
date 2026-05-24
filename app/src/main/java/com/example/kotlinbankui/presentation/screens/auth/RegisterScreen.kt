package com.example.kotlinbankui.presentation.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlinbankui.presentation.components.finsim.ErrorBanner
import com.example.kotlinbankui.presentation.components.finsim.FinSimTextField
import com.example.kotlinbankui.presentation.components.finsim.FinSimTopBar
import com.example.kotlinbankui.presentation.components.finsim.OrderButton

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.registered) {
        if (state.registered) onRegistered()
    }

    Scaffold(
        topBar = { FinSimTopBar(title = "Créer un compte", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenue sur FinSim",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "10 000 \$ fictifs t'attendent à l'inscription",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

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
                value = state.pseudo,
                onValueChange = viewModel::onPseudoChange,
                label = "Pseudo",
                supportingText = "3 à 50 caractères, lettres, chiffres ou _",
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            FinSimTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Mot de passe",
                supportingText = "Minimum 8 caractères",
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
                text = "S'inscrire",
                onClick = viewModel::submit,
                enabled = state.canSubmit,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
