package com.example.kotlinbankui.presentation.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.presentation.screens.auth.RegisterScreen as SharedRegisterScreen

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    SharedRegisterScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPseudoChange = viewModel::onPseudoChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::submit,
        onRegistered = onRegistered,
        onBack = onBack
    )
}
