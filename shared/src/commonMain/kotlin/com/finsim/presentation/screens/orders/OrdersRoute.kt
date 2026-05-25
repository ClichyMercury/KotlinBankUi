package com.finsim.presentation.screens.orders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.finsim.presentation.components.AppBottomBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrdersRoute(
    navController: NavController,
    viewModel: OrdersViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    OrdersScreen(
        state = state,
        onRefresh = viewModel::refresh,
        bottomBar = { AppBottomBar(navController) }
    )
}
