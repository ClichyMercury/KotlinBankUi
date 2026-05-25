package com.example.kotlinbankui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.finsim.data.auth.SessionManager
import com.finsim.ui.theme.ThemePreference
import com.example.kotlinbankui.data.auth.ThemePreferenceStore
import com.example.kotlinbankui.presentation.navigation.BankNavigation
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.finsim.ui.theme.FinSimTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var themePreferenceStore: ThemePreferenceStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
    }
}
