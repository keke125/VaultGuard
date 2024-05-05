package com.keke125.vaultguard

sealed class Screen(val route : String) {
    data object Vault: Screen("vault_screen")
    data object PasswordGenerator: Screen("password_generator_screen")
    data object Setting: Screen("setting_screen")
    data object Login: Screen("login_screen")
    data object Signup: Screen("signup_screen")
}