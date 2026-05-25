package com.finsim.presentation.screens.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.finsim.presentation.navigation.NavigationRoutes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssetDetailRoute(
    navController: NavController,
    assetId: String,
    viewModel: AssetDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(assetId) { viewModel.load(assetId) }

    AssetDetailScreen(
        state = state,
        onBack = { navController.popBackStack() },
        onBuy = { navController.navigate(NavigationRoutes.buy(assetId)) },
        onSelectPeriod = viewModel::selectPeriod,
        onRetry = { viewModel.load(assetId) }
    )
}
