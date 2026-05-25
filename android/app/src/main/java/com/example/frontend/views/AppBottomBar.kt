package com.example.frontend.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun AppBottomBar(
    selectedRoute: String,
    navController: NavController
) {
    val background = Color(0xFF1B221E)
    val green = Color(0xFF7BC88A)
    val muted = Color(0xFFA7B2AA)

    NavigationBar(containerColor = background) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { navController.navigateMain("home") },
            icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
            label = { Text("Inventory") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = green,
                selectedTextColor = green,
                unselectedIconColor = muted,
                unselectedTextColor = muted,
                indicatorColor = Color(0xFF26362A)
            )
        )

        NavigationBarItem(
            selected = selectedRoute == "orders",
            onClick = { navController.navigateMain("orders") },
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = green,
                selectedTextColor = green,
                unselectedIconColor = muted,
                unselectedTextColor = muted,
                indicatorColor = Color(0xFF26362A)
            )
        )

        NavigationBarItem(
            selected = selectedRoute == "profile",
            onClick = { navController.navigateMain("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = green,
                selectedTextColor = green,
                unselectedIconColor = muted,
                unselectedTextColor = muted,
                indicatorColor = Color(0xFF26362A)
            )
        )
    }
}

private fun NavController.navigateMain(route: String) {
    navigate(route) {
        popUpTo("home") {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
