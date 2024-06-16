package com.keke125.vaultguard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.keke125.vaultguard.R

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Default.Lock,
    val route: String = "",
    val navController: NavController
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = navController.context.getString(R.string.app_vault_repository),
                icon = Icons.Default.Lock,
                route = Screen.Vault.route,
                navController
            ), BottomNavigationItem(
                label = navController.context.getString(R.string.app_folder),
                icon = Icons.Default.Folder,
                route = Screen.Folder.route,
                navController
            ), BottomNavigationItem(
                label = navController.context.getString(R.string.app_password_generator_screen_title),
                icon = Icons.Default.Refresh,
                route = Screen.PasswordGenerator.route,
                navController
            ), BottomNavigationItem(
                label = navController.context.getString(R.string.app_setting_title),
                icon = Icons.Default.Settings,
                route = Screen.Setting.route,
                navController
            )
        )
    }
}
