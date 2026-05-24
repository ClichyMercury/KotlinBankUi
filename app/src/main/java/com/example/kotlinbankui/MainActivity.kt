package com.example.kotlinbankui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.kotlinbankui.data.auth.SessionManager
import com.example.kotlinbankui.presentation.navigation.BankNavigation
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes
import com.example.kotlinbankui.ui.theme.FinSimTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinSimTheme {
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
