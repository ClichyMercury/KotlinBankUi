package com.finsim.presentation.screens.trading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BuyRoute(
    navController: NavController,
    assetId: String,
    viewModel: BuyViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    BuyScreen(
        state = state,
        onBack = { navController.popBackStack() },
        onQuantityChange = viewModel::onQuantityChange,
        onPreset = viewModel::applyPercent,
        onSubmit = viewModel::submit,
        onRetry = { viewModel.load(assetId) },
        onDone = { navController.popBackStack() }
    )
}
