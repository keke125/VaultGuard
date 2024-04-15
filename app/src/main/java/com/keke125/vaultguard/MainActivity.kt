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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.keke125.vaultguard.screen.PasswordGeneratorScreen
import com.keke125.vaultguard.screen.SettingScreen
import com.keke125.vaultguard.screen.VaultScreen
import com.keke125.vaultguard.ui.login.LoginActivity
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                NavigationBar {
                    BottomNavigationItem().bottomNavigationItems()
                        .forEachIndexed { _, navigationItem ->
                            NavigationBarItem(
                                selected = navigationItem.route == currentDestination?.route,
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
                                }
                            )
                        }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.UnAuthenticated.UnAuthenticatedRoute.route,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {/*
            composable(Screen.Authenticated.Vault.route) {
                VaultScreen(navController = navController)
            }
            composable(Screen.Authenticated.PasswordGenerator.route) {
                PasswordGeneratorScreen(navController = navController)
            }
            composable(Screen.Authenticated.Setting.route) {
                SettingScreen(navController = navController)
            }*/
            unAuthenticatedGraph()
            authenticatedGraph(navController)
        }
    }
}

fun NavGraphBuilder.authenticatedGraph(navController: NavController) {
    navigation(startDestination = Screen.Authenticated.Vault.route, route = Screen.Authenticated.AuthenticatedRoute.route ) {
        composable(Screen.Authenticated.Vault.route) {
            VaultScreen(navController = navController)
        }
        composable(Screen.Authenticated.PasswordGenerator.route) {
            PasswordGeneratorScreen(navController = navController)
        }
        composable(Screen.Authenticated.Setting.route) {
            SettingScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.unAuthenticatedGraph() {
    navigation(startDestination = Screen.UnAuthenticated.Login.route, route = Screen.UnAuthenticated.UnAuthenticatedRoute.route ) {
        activity(Screen.UnAuthenticated.Login.route) {
            label = "login_screen"
            activityClass = LoginActivity::class
        }
    }
}