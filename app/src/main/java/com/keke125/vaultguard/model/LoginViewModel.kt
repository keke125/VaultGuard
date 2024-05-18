package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.AuthPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.springframework.security.crypto.password.PasswordEncoder

class LoginViewModel(
    authPreferencesRepository: AuthPreferencesRepository,
    private val passwordEncoder: PasswordEncoder
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
        return passwordEncoder.matches(loginPassword, loginPasswordHashed)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class LoginUiState(
    val loginPasswordHashed: String = "",
)