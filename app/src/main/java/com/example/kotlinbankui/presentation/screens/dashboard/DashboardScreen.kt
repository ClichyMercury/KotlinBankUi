package com.example.kotlinbankui.presentation.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.AppBottomBar
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.finsim.presentation.screens.dashboard.DashboardScreen as SharedDashboardScreen

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    SharedDashboardScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onGoToMarket = { navController.navigate(NavigationRoutes.MARKET) },
        onGoToOrders = { navController.navigate(NavigationRoutes.ORDERS) },
        onAssetClick = { id -> navController.navigate(NavigationRoutes.assetDetail(id)) },
        bottomBar = { AppBottomBar(navController) }
    )
}
