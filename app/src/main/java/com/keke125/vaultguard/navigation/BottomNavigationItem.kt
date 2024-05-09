package com.keke125.vaultguard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.keke125.vaultguard.Screen

data class BottomNavigationItem(
    val label: String = "", val icon: ImageVector = Icons.Default.Lock, val route: String = ""
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Vault", icon = Icons.Default.Lock, route = Screen.Vault.route
            ), BottomNavigationItem(
                label = "Password Generator",
                icon = Icons.Default.Refresh,
                route = Screen.PasswordGenerator.route
            ), BottomNavigationItem(
                label = "Search", icon = Icons.Default.Search, route = Screen.SearchVault.route
            )
        )
    }
}
