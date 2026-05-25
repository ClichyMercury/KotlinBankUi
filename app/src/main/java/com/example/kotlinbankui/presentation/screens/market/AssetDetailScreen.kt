package com.example.kotlinbankui.presentation.screens.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.finsim.presentation.screens.market.AssetDetailScreen as SharedAssetDetailScreen

@Composable
fun AssetDetailScreen(
    navController: NavController,
    assetId: String,
    viewModel: AssetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    SharedAssetDetailScreen(
        state = state,
        onBack = { navController.popBackStack() },
        onBuy = { navController.navigate(NavigationRoutes.buy(assetId)) },
        onSelectPeriod = viewModel::selectPeriod,
        onRetry = { viewModel.load(assetId) }
    )
}
