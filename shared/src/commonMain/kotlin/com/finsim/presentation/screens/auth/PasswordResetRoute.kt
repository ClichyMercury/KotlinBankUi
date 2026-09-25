package com.finsim.presentation.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PasswordResetRoute(
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: PasswordResetViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    PasswordResetScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onCodeChange = viewModel::onCodeChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onRequestCode = viewModel::requestCode,
        onSubmitNewPassword = viewModel::submitNewPassword,
        onBackToRequestCode = viewModel::backToRequestCode,
        onGoToEnterCode = viewModel::goToEnterCode,
        onDone = onDone,
        onBack = onBack
    )
}
