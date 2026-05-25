package com.finsim.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.finsim.presentation.components.ErrorBanner
import com.finsim.presentation.components.FinSimTextField
import com.finsim.presentation.components.FinSimTopBar
import com.finsim.presentation.components.OrderButton
import com.finsim.resources.Res
import com.finsim.resources.logo_landscape
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPseudoChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRegistered: () -> Unit,
    onBack: () -> Unit
) {
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

            Image(
                painter = painterResource(Res.drawable.logo_landscape),
                contentDescription = "FinSim",
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Démarrons avec\n10 000 \$ fictifs.",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Crée ton compte FinSim et trade sur des marchés réels sans risque.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            FinSimTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "ton@email.com",
                enabled = !state.isLoading,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            FinSimTextField(
                value = state.pseudo,
                onValueChange = onPseudoChange,
                label = "Pseudo",
                supportingText = "3-50 caractères : lettres, chiffres ou _",
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            FinSimTextField(
                value = state.password,
                onValueChange = onPasswordChange,
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

            Spacer(modifier = Modifier.height(32.dp))

            OrderButton(
                text = "Créer mon compte",
                onClick = onSubmit,
                enabled = state.canSubmit,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
