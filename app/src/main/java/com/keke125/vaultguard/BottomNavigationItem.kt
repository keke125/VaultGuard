package com.keke125.vaultguard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Default.Lock,
    val route: String = ""
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Vault",
                icon = Icons.Default.Lock,
                route = Screen.Authenticated.Vault.route
            ),
            BottomNavigationItem(
                label = "Password Generator",
                icon = Icons.Default.Refresh,
                route = Screen.Authenticated.PasswordGenerator.route
            ),
            BottomNavigationItem(
                label = "Setting",
                icon = Icons.Default.Settings,
                route = Screen.Authenticated.Setting.route
            ),
            BottomNavigationItem(
                label = "Login",
                icon = Icons.Default.AccountBox,
                route = Screen.Login.route
            )
        )
    }
}
