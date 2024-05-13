package com.keke125.vaultguard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.keke125.vaultguard.Screen

data class BottomNavigationItem(
    val label: String = "", val icon: ImageVector = Icons.Default.Lock, val route: String = ""
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "密碼庫", icon = Icons.Default.Lock, route = Screen.Vault.route
            ), BottomNavigationItem(
                label = "密碼產生器",
                icon = Icons.Default.Refresh,
                route = Screen.PasswordGenerator.route
            ), BottomNavigationItem(
                label = "搜尋密碼", icon = Icons.Default.Search, route = Screen.SearchVault.route
            ), BottomNavigationItem(
                label = "設定", icon = Icons.Default.Settings, route = Screen.Setting.route
            )
        )
    }
}
