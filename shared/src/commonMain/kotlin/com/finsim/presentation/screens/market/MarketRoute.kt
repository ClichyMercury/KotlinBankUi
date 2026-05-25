package com.finsim.presentation.screens.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.finsim.presentation.components.AppBottomBar
import com.finsim.presentation.navigation.NavigationRoutes
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MarketRoute(
    navController: NavController,
    viewModel: MarketViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                viewModel.refresh()
                delay(30_000L)
            }
        }
    }

    MarketScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onSelectType = viewModel::selectType,
        onAssetClick = { id -> navController.navigate(NavigationRoutes.assetDetail(id.toString())) },
        bottomBar = { AppBottomBar(navController) }
    )
}
