package com.finsim.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.finsim.data.auth.SessionManager
import com.finsim.data.preferences.ThemePreferenceStore
import com.finsim.presentation.navigation.BankNavigation
import com.finsim.presentation.navigation.NavigationRoutes
import com.finsim.ui.theme.FinSimTheme
import com.finsim.ui.theme.ThemePreference
import org.koin.compose.koinInject

/**
 * Cross-platform entry point. Resolves theme + session services via Koin and hosts
 * the BankNavigation graph.
 *
 * Android: called from MainActivity's setContent { }.
 * iOS: called from ComposeUIViewController { FinSimAppRoot() } in MainViewController.kt.
 */
@Composable
fun FinSimAppRoot() {
    val sessionManager: SessionManager = koinInject()
    val themePreferenceStore: ThemePreferenceStore = koinInject()

    val themePref by themePreferenceStore.preference.collectAsState(initial = ThemePreference.System)
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themePref) {
        ThemePreference.Light -> false
        ThemePreference.Dark -> true
        ThemePreference.System -> systemDark
    }

    FinSimTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                sessionManager.loggedOut.collect {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            BankNavigation(navController = navController)
        }
    }
}
