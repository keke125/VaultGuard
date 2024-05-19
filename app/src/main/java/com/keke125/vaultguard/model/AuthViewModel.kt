package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.service.PasswordService

class AuthViewModel(
    private val passwordService: PasswordService
) : ViewModel() {

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

}