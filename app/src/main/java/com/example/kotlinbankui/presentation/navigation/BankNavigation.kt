package com.example.kotlinbankui.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.kotlinbankui.presentation.components.cards.detail.CardDetailScreen
import com.example.kotlinbankui.presentation.screens.home.HomeScreen
import com.example.kotlinbankui.presentation.screens.notifications.NotificationsScreen
import com.example.kotlinbankui.presentation.screens.profile.ProfileScreen
import com.example.kotlinbankui.presentation.screens.transactions.TransactionsScreen
import com.example.kotlinbankui.presentation.screens.wallet.WalletScreen
import com.example.kotlinbankui.presentation.screens.transactions.detail.TransactionDetailScreen
import com.example.kotlinbankui.presentation.screens.notifications.detail.NotificationDetailScreen

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

        // Detail screens with parameters
        composable(
            route = NavigationRoutes.TRANSACTION_DETAIL,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId
            )
        }

        composable(
            route = NavigationRoutes.CARD_DETAIL,
            arguments = listOf(
                navArgument("cardId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
            CardDetailScreen(
                navController = navController,
                cardId = cardId
            )
        }

        composable(
            route = NavigationRoutes.NOTIFICATION_DETAIL,
            arguments = listOf(
                navArgument("notificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            NotificationDetailScreen(
                navController = navController,
                notificationId = notificationId
            )
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