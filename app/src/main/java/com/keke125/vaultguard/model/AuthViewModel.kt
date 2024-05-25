package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.service.PasswordService

class AuthViewModel(
    private val passwordService: PasswordService
) : ViewModel() {

    private val authUiState by mutableStateOf(AuthUiState())

    fun updateMainPassword(mainPassword: String) {
        passwordService.updatePassword(mainPassword)
    }

    fun checkMainPassword(mainPassword: String): Boolean {
        val isUserAuthenticated = passwordService.validatePassword(mainPassword)
        return isUserAuthenticated
    }

    fun isSignup(): Boolean{
        return passwordService.isSignup()
    }

    fun isAuthenticated(): Boolean{
        return passwordService.isAuthenticated()
    }

    fun logout(){
        passwordService.logout()
    }

    fun authWithBiometric(isAuthenticatedSuccess: Boolean){
        passwordService.authenticateWithBiometric(isAuthenticatedSuccess)
        authUiState.isAuthenticated = isAuthenticatedSuccess
    }

    fun isNotTimeout(): Boolean {
        return passwordService.isNotTimeout()
    }

}

data class AuthUiState(
    var isAuthenticated: Boolean = false
)