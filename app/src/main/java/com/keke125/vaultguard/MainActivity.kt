package com.keke125.vaultguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.keke125.vaultguard.data.AppDB
import com.keke125.vaultguard.data.OfflineVaultsRepository
import com.keke125.vaultguard.data.VaultDAO
import com.keke125.vaultguard.screen.AddVaultScreen
import com.keke125.vaultguard.screen.LoginScreen
import com.keke125.vaultguard.screen.PasswordGeneratorScreen
import com.keke125.vaultguard.screen.SettingScreen
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
        db = AppDB.getDatabase(this)
        vaultDAO = db.vaultDAO()
        offlineVaultsRepository = OfflineVaultsRepository(vaultDAO)
    }
    companion object {
        private lateinit var db: AppDB
        private lateinit var vaultDAO: VaultDAO
        private lateinit var offlineVaultsRepository: OfflineVaultsRepository
        fun getOfflineVaultsRepository() : OfflineVaultsRepository{
            return offlineVaultsRepository
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(bottomBar = {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { _, navigationItem ->
                        NavigationBarItem(selected = navigationItem.route == currentDestination?.route,
                            label = {
                                Text(navigationItem.label)
                            },
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label
                                )
                            },
                            onClick = {
                                navController.navigate(navigationItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            })
                    }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Vault.route,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            composable(Screen.Vault.route) {
                AddVaultScreen(navController = navController)
            }
            composable(Screen.PasswordGenerator.route) {
                PasswordGeneratorScreen(navController = navController)
            }
            composable(Screen.Setting.route) {
                SettingScreen(navController = navController)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }
        }
    }
}