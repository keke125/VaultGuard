package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.UserPreferencesRepository
import com.keke125.vaultguard.service.PasswordService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BiometricAuthSettingViewModel(private val userPreferencesRepository: UserPreferencesRepository) :
    ViewModel() {
    val uiState: StateFlow<BiometricAuthUiState> =
        userPreferencesRepository.isBiometricEnabled.map { isBiometricEnabled ->
            BiometricAuthUiState(isBiometricEnabled)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = BiometricAuthUiState()
        )

    fun updateBiometricEnabled(isBiometricEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveBiometricPreference(isBiometricEnabled)
        }
    }

    fun updateCanAuthenticateWithBiometrics(canAuthenticateWithBiometrics: Boolean) {
        uiState.value.canAuthenticateWithBiometrics = canAuthenticateWithBiometrics
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class BiometricAuthUiState(
    val isBiometricEnabled: Boolean = false, var canAuthenticateWithBiometrics: Boolean = false
)