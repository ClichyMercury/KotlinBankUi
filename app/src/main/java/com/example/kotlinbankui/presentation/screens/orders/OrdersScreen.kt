package com.example.kotlinbankui.presentation.screens.orders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.AppBottomBar
import com.finsim.presentation.screens.orders.OrdersScreen as SharedOrdersScreen

@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    SharedOrdersScreen(
        state = state,
        onRefresh = viewModel::refresh,
        bottomBar = { AppBottomBar(navController) }
    )
}
