package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.service.PasswordService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val passwordService: PasswordService
) : ViewModel() {

    private val _authUiState = MutableStateFlow(
        AuthUiState(
            isAuthenticated = passwordService.isAuthenticated() && passwordService.isNotTimeout()
        )
    )
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun refreshAuthStatus() {
        _authUiState.update {
            it.copy(
                isAuthenticated = passwordService.isAuthenticated() && passwordService.isNotTimeout()
            )
        }
    }

    fun updateMainPassword(mainPassword: String) {
        passwordService.updatePassword(mainPassword)
    }

    fun checkMainPassword(mainPassword: String): Boolean {
        val isUserAuthenticated = passwordService.validatePassword(mainPassword)
        if (isUserAuthenticated) {
            refreshAuthStatus()
        }
        return isUserAuthenticated
    }

    fun isSignup(): Boolean {
        return passwordService.isSignup()
    }

    fun isAuthenticated(): Boolean {
        return passwordService.isAuthenticated()
    }

    fun logout() {
        passwordService.logout()
        refreshAuthStatus()
    }

    fun authWithBiometric(isAuthenticatedSuccess: Boolean) {
        passwordService.authenticateWithBiometric(isAuthenticatedSuccess)
        refreshAuthStatus()
    }

    fun isNotTimeout(): Boolean {
        return passwordService.isNotTimeout()
    }
}

data class AuthUiState(
    var isAuthenticated: Boolean = false
)