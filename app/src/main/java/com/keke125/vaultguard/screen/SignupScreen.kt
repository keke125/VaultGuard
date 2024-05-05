package com.keke125.vaultguard.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.SignupViewModel
import com.keke125.vaultguard.model.isValidEmail
import com.keke125.vaultguard.model.isValidPassword
import com.keke125.vaultguard.model.isValidRepeatPassword
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: SignupViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val activity = navController.context as Activity
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (isRepeatPasswordVisible, onRepeatPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Sign up", fontSize = 36.sp)
                OutlinedTextField(value = viewModel.signupUiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = {
                        Text(
                            text = "Username (Email)"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle, contentDescription = ""
                        )
                    },
                    singleLine = true,
                    placeholder = { Text(text = "Please enter Email") },
                    isError = !viewModel.signupUiState.email.isValidEmail() && viewModel.signupUiState.email.isNotEmpty(),
                    supportingText = {
                        if (!viewModel.signupUiState.email.isValidEmail() && viewModel.signupUiState.email.isNotEmpty()) Text(
                            "Please enter a valid email address"
                        )
                    })
                OutlinedTextField(value = viewModel.signupUiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = {
                        Text(
                            text = "Password"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock, contentDescription = ""
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { onPasswordVisibleChange(!isPasswordVisible) }) {
                            Icon(
                                imageVector = if (isPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                }, contentDescription = ""
                            )
                        }
                    },
                    singleLine = true,
                    placeholder = { Text(text = "Please enter password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = !viewModel.signupUiState.password.isValidPassword() && viewModel.signupUiState.password.isNotEmpty(),
                    supportingText = {
                        if (!viewModel.signupUiState.password.isValidPassword() && viewModel.signupUiState.password.isNotEmpty()) Text(
                            "Password must be at least 6 characters"
                        )
                    })
                OutlinedTextField(value = viewModel.signupUiState.repeatPassword,
                    onValueChange = { viewModel.updateRepeatPassword(it) },
                    label = {
                        Text(
                            text = "Repeat Password"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock, contentDescription = ""
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { onRepeatPasswordVisibleChange(!isRepeatPasswordVisible) }) {
                            Icon(
                                imageVector = if (isRepeatPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                }, contentDescription = ""
                            )
                        }
                    },
                    singleLine = true,
                    placeholder = { Text(text = "Please enter password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (isRepeatPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = !viewModel.signupUiState.repeatPassword.isValidRepeatPassword(
                        viewModel.signupUiState.password
                    ) && viewModel.signupUiState.repeatPassword.isNotEmpty(),
                    supportingText = {
                        if (!viewModel.signupUiState.repeatPassword.isValidRepeatPassword(
                                viewModel.signupUiState.password
                            ) && viewModel.signupUiState.repeatPassword.isNotEmpty()
                        ) Text("Passwords do not match")
                    })
                Button(onClick = {
                    if (viewModel.signupUiState.email.isValidEmail() && viewModel.signupUiState.password.isValidPassword() && viewModel.signupUiState.repeatPassword.isValidRepeatPassword(
                            viewModel.signupUiState.password
                        )
                    ) viewModel.onSignUpClick(
                        context, activity
                    )
                    else Toast.makeText(
                        context, "Please fill all fields with valid data", Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text(text = "Sign up")
                }
            }
        }
    }
}