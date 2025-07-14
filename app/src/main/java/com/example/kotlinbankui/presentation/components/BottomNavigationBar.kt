package com.example.kotlinbankui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AllInbox
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinbankui.domain.models.BottomNavigationItem
import com.example.kotlinbankui.presentation.navigation.NavigationRoutes

val items = listOf(
    BottomNavigationItem(
        title = "Home",
        icon = Icons.Rounded.Home,
        route = NavigationRoutes.HOME
    ),
    BottomNavigationItem(
        title = "Wallet",
        icon = Icons.Rounded.Wallet,
        route = NavigationRoutes.WALLET
    ),
    BottomNavigationItem(
        title = "Transactions",
        icon = Icons.Rounded.AllInbox,
        route = NavigationRoutes.TRANSACTIONS
    ),
    BottomNavigationItem(
        title = "Notifications",
        icon = Icons.Rounded.Notifications,
        route = NavigationRoutes.NOTIFICATIONS
    ),
    BottomNavigationItem(
        title = "Account",
        icon = Icons.Rounded.AccountCircle,
        route = NavigationRoutes.PROFILE
    ),
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = {
                        // Afficher le titre seulement si l'onglet est actif
                        if (currentRoute == item.route) {
                            Text(
                                text = item.title,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    modifier = Modifier.weight(
                        // Plus d'espace pour l'onglet sélectionné
                        if (currentRoute == item.route) 2f else 1f
                    )
                )
            }
        }
    }
}