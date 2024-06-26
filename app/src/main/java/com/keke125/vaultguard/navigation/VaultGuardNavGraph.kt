package com.keke125.vaultguard.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keke125.vaultguard.screen.AddFolderDestination
import com.keke125.vaultguard.screen.AddFolderScreen
import com.keke125.vaultguard.screen.AddVaultDestination
import com.keke125.vaultguard.screen.AddVaultScreen
import com.keke125.vaultguard.screen.EditFolderDestination
import com.keke125.vaultguard.screen.EditFolderScreen
import com.keke125.vaultguard.screen.EditVaultDestination
import com.keke125.vaultguard.screen.EditVaultScreen
import com.keke125.vaultguard.screen.FolderDetailsDestination
import com.keke125.vaultguard.screen.FolderDetailsScreen
import com.keke125.vaultguard.screen.FolderScreen
import com.keke125.vaultguard.screen.PasswordGeneratorScreen
import com.keke125.vaultguard.screen.SearchVaultByFolderUidDestination
import com.keke125.vaultguard.screen.SearchVaultByFolderUidScreen
import com.keke125.vaultguard.screen.SearchVaultScreen
import com.keke125.vaultguard.screen.SettingScreen
import com.keke125.vaultguard.screen.SignupScreen
import com.keke125.vaultguard.screen.VaultDetailsDestination
import com.keke125.vaultguard.screen.VaultDetailsScreen
import com.keke125.vaultguard.screen.VaultScreen
import com.keke125.vaultguard.screen.VaultsRepositoryScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(bottomBar = {
        if (currentDestination?.route == Screen.Vault.route || currentDestination?.route == Screen.PasswordGenerator.route || currentDestination?.route == Screen.Setting.route || currentDestination?.route == Screen.Folder.route) {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                NavigationBar {
                    BottomNavigationItem(navController = navController).bottomNavigationItems()
                        .forEachIndexed { _, navigationItem ->
                            NavigationBarItem(selected = navigationItem.route == currentDestination.route,
                                label = {
                                    Text(navigationItem.label, textAlign = TextAlign.Center)
                                },
                                icon = {
                                    Icon(
                                        navigationItem.icon,
                                        contentDescription = navigationItem.label
                                    )
                                },
                                onClick = {
                                    navController.navigate(navigationItem.route)
                                })
                        }
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Vault.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Vault.route) {
                VaultScreen(navController = navController,
                    navigateToViewVault = { navController.navigate("${VaultDetailsDestination.route}/${it}") },
                    navigateToEditVault = { navController.navigate("${EditVaultDestination.route}/${it}") },
                    navigateToSearchVault = { navController.navigate(Screen.SearchVault.route) })
            }
            composable(Screen.SearchVault.route) {
                SearchVaultScreen(navController = navController,
                    navigateToViewVault = { navController.navigate("${VaultDetailsDestination.route}/${it}") },
                    navigateToEditVault = { navController.navigate("${EditVaultDestination.route}/${it}") })
            }
            composable(Screen.PasswordGenerator.route) {
                PasswordGeneratorScreen(navController = navController)
            }
            composable(Screen.Setting.route) {
                SettingScreen(navController = navController)
            }
            composable(
                route = VaultDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(VaultDetailsDestination.VAULTID) {
                    type = NavType.IntType
                })
            ) {
                VaultDetailsScreen(navController = navController)
            }
            composable(
                route = AddVaultDestination.routeWithArgs,
                arguments = listOf(navArgument(AddVaultDestination.FOLDERID) {
                    type = NavType.IntType
                })
            ) {
                AddVaultScreen(
                    navController = navController
                )
            }
            composable(
                route = EditVaultDestination.routeWithArgs,
                arguments = listOf(navArgument(EditVaultDestination.VAULTID) {
                    type = NavType.IntType
                })
            ) {
                EditVaultScreen(navController = navController)
            }
            composable(Screen.VaultRepository.route) {
                VaultsRepositoryScreen(navController = navController)
            }
            composable(Screen.Signup.route) {
                SignupScreen(navController = navController)
            }
            composable(Screen.Folder.route) {
                FolderScreen(navController = navController,
                    navigateToViewFolder = { navController.navigate("${FolderDetailsDestination.route}/${it}") })
            }
            composable(AddFolderDestination.route) {
                AddFolderScreen(navController = navController)
            }
            composable(
                route = EditFolderDestination.routeWithArgs,
                arguments = listOf(navArgument(EditFolderDestination.FOLDERID) {
                    type = NavType.IntType
                })
            ) {
                EditFolderScreen(navController = navController)
            }
            composable(
                route = FolderDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(FolderDetailsDestination.FOLDERID) {
                    type = NavType.IntType
                })
            ) {
                FolderDetailsScreen(navController = navController,
                    navigateToViewVault = { navController.navigate("${VaultDetailsDestination.route}/${it}") },
                    navigateToEditVault = { navController.navigate("${EditVaultDestination.route}/${it}") },
                    navigateToSearchVaultByFolderUid = { navController.navigate("${SearchVaultByFolderUidDestination.route}/${it}") })
            }
            composable(
                route = SearchVaultByFolderUidDestination.routeWithArgs,
                arguments = listOf(navArgument(SearchVaultByFolderUidDestination.FOLDERID) {
                    type = NavType.IntType
                })
            ) {
                SearchVaultByFolderUidScreen(
                    navController = navController,
                    navigateToViewVault = { navController.navigate("${VaultDetailsDestination.route}/${it}") },
                    navigateToEditVault = { navController.navigate("${EditVaultDestination.route}/${it}") },
                )
            }
        }
    }
}
