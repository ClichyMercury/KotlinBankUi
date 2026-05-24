package com.example.kotlinbankui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kotlinbankui.presentation.screens.auth.LoginScreen
import com.example.kotlinbankui.presentation.screens.auth.RegisterScreen
import com.example.kotlinbankui.presentation.screens.dashboard.DashboardScreen
import com.example.kotlinbankui.presentation.screens.market.AssetDetailScreen
import com.example.kotlinbankui.presentation.screens.market.MarketScreen
import com.example.kotlinbankui.presentation.screens.orders.OrdersScreen
import com.example.kotlinbankui.presentation.screens.profile.ProfileScreen
import com.example.kotlinbankui.presentation.screens.splash.SplashScreen
import com.example.kotlinbankui.presentation.screens.trading.BuyScreen

@Composable
fun BankNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.SPLASH
    ) {
        composable(NavigationRoutes.SPLASH) {
            SplashScreen(
                onAuthenticated = {
                    navController.navigate(NavigationRoutes.DASHBOARD) {
                        popUpTo(NavigationRoutes.SPLASH) { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(NavigationRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(NavigationRoutes.DASHBOARD) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(NavigationRoutes.REGISTER) }
            )
        }

        composable(NavigationRoutes.REGISTER) {
            RegisterScreen(
                onRegistered = {
                    navController.navigate(NavigationRoutes.DASHBOARD) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }

        composable(NavigationRoutes.MARKET) {
            MarketScreen(navController = navController)
        }

        composable(NavigationRoutes.ORDERS) {
            OrdersScreen(navController = navController)
        }

        composable(NavigationRoutes.PROFILE) {
            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavigationRoutes.ASSET_DETAIL,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            AssetDetailScreen(navController = navController, assetId = assetId)
        }

        composable(
            route = NavigationRoutes.BUY,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            BuyScreen(navController = navController, assetId = assetId)
        }
    }
}
