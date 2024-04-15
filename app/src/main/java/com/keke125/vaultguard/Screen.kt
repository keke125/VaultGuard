package com.keke125.vaultguard

sealed class Screen {
    // Authenticated Routes
    sealed class Authenticated(val route: String): Screen() {
        data object AuthenticatedRoute: Authenticated("authenticated_screen")
        data object Vault: Authenticated("vault_screen")
        data object PasswordGenerator: Authenticated("password_generator_screen")
        data object Setting: Authenticated("setting_screen")
    }

    // UnAuthenticated Routes
    sealed class UnAuthenticated(val route: String): Screen() {
        data object UnAuthenticatedRoute: UnAuthenticated("unauthenticated_screen")
        data object Login: UnAuthenticated("login_screen")
    }
}