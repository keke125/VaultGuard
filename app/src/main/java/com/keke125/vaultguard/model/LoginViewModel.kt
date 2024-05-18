package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.AuthPreferencesRepository
import com.keke125.vaultguard.service.PasswordService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LoginViewModel(
    authPreferencesRepository: AuthPreferencesRepository,
    private val passwordService: PasswordService
) : ViewModel() {

    val loginUiState: StateFlow<LoginUiState> =
        authPreferencesRepository.loginPasswordHashed.map { loginPasswordHashed ->
            LoginUiState(loginPasswordHashed)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = LoginUiState()
        )

    fun checkLoginPassword(loginPassword: String, loginPasswordHashed: String): Boolean {
        return passwordService.validatePassword(loginPassword, loginPasswordHashed)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class LoginUiState(
    val loginPasswordHashed: String = "",
)