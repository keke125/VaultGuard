package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.AuthPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.springframework.security.crypto.password.PasswordEncoder

class SignupViewModel(
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val passwordEncoder: PasswordEncoder
) : ViewModel() {

    val signupUiState: StateFlow<SignupUiState> =
        authPreferencesRepository.loginPasswordHashed.map { signupPasswordHashed ->
            SignupUiState(signupPasswordHashed)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SignupUiState()
        )

    fun savePassword(signupPassword: String) {
        viewModelScope.launch {
            authPreferencesRepository.saveAuthPreference(passwordEncoder.encode(signupPassword))
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class SignupUiState(
    val signupPasswordHashed: String = "",
)