package com.keke125.vaultguard.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.AuthPreferencesRepository
import com.keke125.vaultguard.service.PasswordService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val passwordService: PasswordService
) : ViewModel() {

    val mainPasswordHashed: StateFlow<String> = authPreferencesRepository.mainPasswordHashed
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ""
        )

    val mainPasswordHashedLiveData: LiveData<String> = authPreferencesRepository.mainPasswordHashed.asLiveData()

    fun savePassword(mainPassword: String) {
        viewModelScope.launch {
            authPreferencesRepository.saveAuthPreference(passwordService.generatePasswordHash(mainPassword))
        }
    }

    fun checkMainPassword(mainPassword: String, mainPasswordHashed: String): Boolean {
        return passwordService.validatePassword(mainPassword, mainPasswordHashed)
    }
}