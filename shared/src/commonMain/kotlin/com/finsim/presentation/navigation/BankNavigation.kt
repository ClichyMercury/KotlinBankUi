package com.finsim.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.finsim.presentation.screens.auth.LoginRoute
import com.finsim.presentation.screens.auth.RegisterRoute
import com.finsim.presentation.screens.dashboard.DashboardRoute
import com.finsim.presentation.screens.market.AssetDetailRoute
import com.finsim.presentation.screens.market.MarketRoute
import com.finsim.presentation.screens.orders.OrdersRoute
import com.finsim.presentation.screens.profile.ProfileRoute
import com.finsim.presentation.screens.splash.SplashRoute
import com.finsim.presentation.screens.trading.BuyRoute
import com.finsim.presentation.screens.trading.SellRoute

@Composable
fun BankNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.SPLASH
    ) {
        composable(NavigationRoutes.SPLASH) {
            SplashRoute(
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
            LoginRoute(
                onLoggedIn = {
                    navController.navigate(NavigationRoutes.DASHBOARD) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(NavigationRoutes.REGISTER) }
            )
        }

        composable(NavigationRoutes.REGISTER) {
            RegisterRoute(
                onRegistered = {
                    navController.navigate(NavigationRoutes.DASHBOARD) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.DASHBOARD) {
            DashboardRoute(navController = navController)
        }

        composable(NavigationRoutes.MARKET) {
            MarketRoute(navController = navController)
        }

        composable(NavigationRoutes.ORDERS) {
            OrdersRoute(navController = navController)
        }

        composable(NavigationRoutes.PROFILE) {
            ProfileRoute(
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
            val assetId = backStackEntry.arguments?.read { getString("assetId") } ?: ""
            AssetDetailRoute(navController = navController, assetId = assetId)
        }

        composable(
            route = NavigationRoutes.BUY,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.read { getString("assetId") } ?: ""
            BuyRoute(navController = navController, assetId = assetId)
        }

        composable(
            route = NavigationRoutes.SELL,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.read { getString("assetId") } ?: ""
            SellRoute(navController = navController, assetId = assetId)
        }
    }
}
