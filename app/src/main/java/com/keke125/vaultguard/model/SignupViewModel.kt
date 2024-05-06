package com.keke125.vaultguard.model

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.service.AuthService
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authService: AuthService,
) : ViewModel() {

    var signupUiState by mutableStateOf(SignupUiState())
        private set

    private val email
        get() = signupUiState.email
    private val password
        get() = signupUiState.password

    fun onSignUpClick(
        context: Context, activity: Activity, openAndPopUp: () -> Unit
    ) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (password != signupUiState.repeatPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            authService.signupWithEmailAndPassword(email, password, context, activity, openAndPopUp)
        }
    }

    fun updateEmail(email: String) {
        signupUiState = signupUiState.copy(email = email)
    }

    fun updatePassword(password: String) {
        signupUiState = signupUiState.copy(password = password)
    }

    fun updateRepeatPassword(repeatPassword: String) {
        signupUiState = signupUiState.copy(repeatPassword = repeatPassword)
    }

}

data class SignupUiState(
    val email: String = "", val password: String = "", val repeatPassword: String = ""
)

fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isValidPassword(): Boolean = this.length >= 6
fun String.isValidRepeatPassword(password: String): Boolean = this == password