package com.keke125.vaultguard.model

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.service.AuthService
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService,
) : ViewModel() {

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    private val email
        get() = loginUiState.email
    private val password
        get() = loginUiState.password

    fun onLoginClick(
        context: Context, activity: Activity, openAndPopUp: () -> Unit
    ) {

        viewModelScope.launch {
            authService.authenticateWithEmailAndPassword(
                email, password, activity, context, openAndPopUp
            )
        }
    }

    fun updateEmail(email: String) {
        loginUiState = loginUiState.copy(email = email)
    }

    fun updatePassword(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

}

data class LoginUiState(
    val email: String = "", val password: String = ""
)