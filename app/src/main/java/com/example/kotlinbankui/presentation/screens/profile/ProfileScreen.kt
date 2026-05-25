package com.example.kotlinbankui.presentation.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.kotlinbankui.presentation.components.AppBottomBar
import com.finsim.presentation.screens.profile.ProfileScreen as SharedProfileScreen

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    SharedProfileScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onThemeSelect = viewModel::setThemePreference,
        onLogout = viewModel::logout,
        onLoggedOut = onLogout,
        bottomBar = { AppBottomBar(navController) }
    )
}
