package com.finsim.presentation.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.finsim.presentation.components.AppBottomBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoute(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    ProfileScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onThemeSelect = viewModel::setThemePreference,
        onLogout = viewModel::logout,
        onLoggedOut = onLogout,
        bottomBar = { AppBottomBar(navController) }
    )
}
