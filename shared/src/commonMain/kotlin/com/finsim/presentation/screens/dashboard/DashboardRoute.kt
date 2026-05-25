package com.finsim.presentation.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.finsim.presentation.components.AppBottomBar
import com.finsim.presentation.navigation.NavigationRoutes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoute(
    navController: NavController,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    DashboardScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onGoToMarket = { navController.navigate(NavigationRoutes.MARKET) },
        onGoToOrders = { navController.navigate(NavigationRoutes.ORDERS) },
        onAssetClick = { id -> navController.navigate(NavigationRoutes.assetDetail(id)) },
        bottomBar = { AppBottomBar(navController) }
    )
}
