package com.example.kotlinbankui.presentation.components.finsim

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes

private data class FinSimTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    FinSimTab(NavigationRoutes.DASHBOARD, "Portfolio", Icons.Outlined.PieChart),
    FinSimTab(NavigationRoutes.MARKET, "Marché", Icons.AutoMirrored.Outlined.ShowChart),
    FinSimTab(NavigationRoutes.ORDERS, "Ordres", Icons.Outlined.Receipt),
    FinSimTab(NavigationRoutes.PROFILE, "Profil", Icons.Outlined.AccountCircle)
)

@Composable
fun FinSimBottomBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.route) {
                            popUpTo(NavigationRoutes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                label = { Text(text = tab.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
