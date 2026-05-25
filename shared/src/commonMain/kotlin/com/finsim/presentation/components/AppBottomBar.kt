package com.finsim.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.finsim.presentation.navigation.NavigationRoutes

private val tabs = listOf(
    FinSimTab(NavigationRoutes.DASHBOARD, "Portfolio", Icons.Outlined.PieChart),
    FinSimTab(NavigationRoutes.MARKET, "Marché", Icons.AutoMirrored.Outlined.ShowChart),
    FinSimTab(NavigationRoutes.ORDERS, "Ordres", Icons.Outlined.Receipt),
    FinSimTab(NavigationRoutes.PROFILE, "Profil", Icons.Outlined.AccountCircle)
)

@Composable
fun AppBottomBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    FinSimBottomBar(
        tabs = tabs,
        currentRoute = currentRoute,
        onSelect = { tab ->
            navController.navigate(tab.route) {
                popUpTo(NavigationRoutes.DASHBOARD) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}
