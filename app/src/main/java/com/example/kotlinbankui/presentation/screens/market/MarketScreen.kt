package com.example.kotlinbankui.presentation.screens.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.AppBottomBar
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.finsim.presentation.screens.market.MarketScreen as SharedMarketScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.coroutines.delay

@OptIn(ExperimentalUuidApi::class)
@Composable
fun MarketScreen(
    navController: NavController,
    viewModel: MarketViewModel = hiltViewModel()
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

    SharedMarketScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onSelectType = viewModel::selectType,
        onAssetClick = { id -> navController.navigate(NavigationRoutes.assetDetail(id.toString())) },
        bottomBar = { AppBottomBar(navController) }
    )
}
