package com.finsim.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finsim.presentation.components.ErrorBanner
import com.finsim.presentation.components.FinSimTextField
import com.finsim.presentation.components.FinSimTopBar
import com.finsim.presentation.components.OrderButton

@Composable
fun PasswordResetScreen(
    state: PasswordResetUiState,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onRequestCode: () -> Unit,
    onSubmitNewPassword: () -> Unit,
    onBackToRequestCode: () -> Unit,
    onGoToEnterCode: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinSimTopBar(
                title = "Mot de passe oublié",
                onBack = if (state.step == PasswordResetStep.Done) null else onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            when (state.step) {
                PasswordResetStep.RequestCode -> RequestCodeStep(
                    state = state,
                    onEmailChange = onEmailChange,
                    onRequestCode = onRequestCode,
                    onGoToEnterCode = onGoToEnterCode
                )

                PasswordResetStep.EnterCode -> EnterCodeStep(
                    state = state,
                    onCodeChange = onCodeChange,
                    onNewPasswordChange = onNewPasswordChange,
                    onSubmitNewPassword = onSubmitNewPassword,
                    onBackToRequestCode = onBackToRequestCode
                )

                PasswordResetStep.Done -> DoneStep(onDone = onDone)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun RequestCodeStep(
    state: PasswordResetUiState,
    onEmailChange: (String) -> Unit,
    onRequestCode: () -> Unit,
    onGoToEnterCode: () -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "On t'envoie un code.",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.ExtraBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Entre l'email de ton compte. Tu recevras un code de réinitialisation valable 30 minutes.",
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

    state.errorMessage?.let { msg ->
        Spacer(modifier = Modifier.height(16.dp))
        ErrorBanner(message = msg)
    }

    Spacer(modifier = Modifier.height(32.dp))

    OrderButton(
        text = "Envoyer le code",
        onClick = onRequestCode,
        enabled = state.canRequestCode,
        isLoading = state.isLoading
    )

    Spacer(modifier = Modifier.height(12.dp))

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = onGoToEnterCode, enabled = !state.isLoading) {
            Text(
                text = "J'ai déjà un code",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EnterCodeStep(
    state: PasswordResetUiState,
    onCodeChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onSubmitNewPassword: () -> Unit,
    onBackToRequestCode: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Nouveau mot de passe.",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.ExtraBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.infoMessage ?: "Colle le code reçu par email, puis choisis ton nouveau mot de passe.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(32.dp))

    FinSimTextField(
        value = state.code,
        onValueChange = onCodeChange,
        label = "Code de réinitialisation",
        placeholder = "Colle le code de l'email",
        enabled = !state.isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    FinSimTextField(
        value = state.newPassword,
        onValueChange = onNewPasswordChange,
        label = "Nouveau mot de passe",
        supportingText = "8 caractères minimum",
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
        text = "Réinitialiser",
        onClick = onSubmitNewPassword,
        enabled = state.canSubmitNewPassword,
        isLoading = state.isLoading
    )

    Spacer(modifier = Modifier.height(12.dp))

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = onBackToRequestCode, enabled = !state.isLoading) {
            Text(
                text = "Renvoyer un code",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DoneStep(onDone: () -> Unit) {
    Spacer(modifier = Modifier.height(48.dp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mot de passe mis à jour",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Toutes tes sessions ont été déconnectées, sur cet appareil comme sur les autres. Connecte-toi avec ton nouveau mot de passe.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(40.dp))

    OrderButton(
        text = "Retour à la connexion",
        onClick = onDone
    )
}
