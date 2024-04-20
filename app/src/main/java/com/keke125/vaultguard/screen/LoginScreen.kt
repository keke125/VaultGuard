package com.keke125.vaultguard.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.navigation.NavController
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

@Composable
fun LoginScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val (username, onUsernameChange) = remember {
                mutableStateOf("")
            }
            val (password, onPasswordChange) = remember {
                mutableStateOf("")
            }
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Login", fontSize = 36.sp)
                OutlinedTextField(
                    value = username,
                    onValueChange = { onUsernameChange(it) },
                    label = {
                        Text(
                            text = "Username"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle, contentDescription = ""
                        )
                    },
                    singleLine = true,
                    placeholder = { Text(text = "Please enter username") },
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { onPasswordChange(it) },
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
                    }
                )
            }
        }
    }
}