package com.example.kotlinbankui.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlinbankui.presentation.screens.home.HomeScreen
import com.example.kotlinbankui.presentation.screens.notifications.NotificationsScreen
import com.example.kotlinbankui.presentation.screens.profile.ProfileScreen
import com.example.kotlinbankui.presentation.screens.transactions.TransactionsScreen
import com.example.kotlinbankui.presentation.screens.wallet.WalletScreen

@Composable
fun BankNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOME
    ) {
        composable(NavigationRoutes.HOME) {
            HomeScreen(navController)
        }

        composable(NavigationRoutes.WALLET) {
            WalletScreen(navController)
        }

        composable(NavigationRoutes.TRANSACTIONS) {
            TransactionsScreen(navController)
        }

        composable(NavigationRoutes.NOTIFICATIONS) {
            NotificationsScreen(navController)
        }

        composable(NavigationRoutes.PROFILE) {
            ProfileScreen(navController)
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = title,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
    }
}