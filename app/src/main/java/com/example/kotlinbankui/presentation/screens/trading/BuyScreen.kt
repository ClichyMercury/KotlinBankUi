package com.example.kotlinbankui.presentation.screens.trading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.finsim.presentation.screens.trading.BuyScreen as SharedBuyScreen

@Composable
fun BuyScreen(
    navController: NavController,
    assetId: String,
    viewModel: BuyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    SharedBuyScreen(
        state = state,
        onBack = { navController.popBackStack() },
        onQuantityChange = viewModel::onQuantityChange,
        onPreset = viewModel::applyPercent,
        onSubmit = viewModel::submit,
        onRetry = { viewModel.load(assetId) },
        onDone = { navController.popBackStack() }
    )
}
