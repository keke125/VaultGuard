package com.keke125.vaultguard

sealed class Screen(val route : String) {
    data object Vault: Screen("vault_screen")
    data object PasswordGenerator: Screen("password_generator_screen")
    data object Setting: Screen("setting_screen")
    data object SearchVault: Screen("search_vault_screen")
    data object VaultRepository: Screen("vault_repository_screen")
    data object BiometricAuth: Screen("biometric_auth_screen")
}