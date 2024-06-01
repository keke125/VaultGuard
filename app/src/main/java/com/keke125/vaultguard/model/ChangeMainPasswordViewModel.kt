package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.service.PasswordService

class ChangeMainPasswordViewModel(private val passwordService: PasswordService) : ViewModel() {

    var changeMainPasswordUiState by mutableStateOf(ChangeMainPasswordUiState())
        private set

    fun updateUiState(changeMainPassword: ChangeMainPassword) {
        changeMainPasswordUiState =
            ChangeMainPasswordUiState(changeMainPassword = changeMainPassword)
    }

    fun changeMainPassword(): Boolean {
        return passwordService.changeMainPassword(
            changeMainPasswordUiState.changeMainPassword.oldPassword,
            changeMainPasswordUiState.changeMainPassword.newPassword
        )
    }
}

data class ChangeMainPasswordUiState(
    val changeMainPassword: ChangeMainPassword = ChangeMainPassword()
)

data class ChangeMainPassword(
    var oldPassword: String = "",
    var newPassword: String = "",
)

