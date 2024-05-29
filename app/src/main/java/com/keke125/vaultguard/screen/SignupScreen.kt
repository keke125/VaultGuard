package com.keke125.vaultguard.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.Screen
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.AuthViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val (signupPassword, onSignupPasswordChange) = remember { mutableStateOf("") }
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            if (!viewModel.isSignup()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_launcher),
                        contentDescription = "",
                        modifier = Modifier.size(128.dp)
                    )
                    Text(text = "設定主密碼", fontSize = 24.sp)
                    OutlinedTextField(
                        value = signupPassword,
                        onValueChange = { onSignupPasswordChange(it) },
                        label = {
                            Text(
                                text = "主密碼"
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
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    }, contentDescription = ""
                                )
                            }
                        },
                        singleLine = true,
                        placeholder = { Text(text = "請輸入密碼") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Button(onClick = {
                        if (signupPassword.isEmpty() || signupPassword.isBlank()) {
                            Toast.makeText(
                                context, "請輸入主密碼!", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context, "設定成功", Toast.LENGTH_SHORT
                            ).show()
                            viewModel.updateMainPassword(signupPassword)
                            navController.navigate(Screen.Vault.route)
                        }
                    }) {
                        Text("設定")
                    }
                }
            }else{
                navController.navigate(Screen.Vault.route)
            }
        }
    }
    BackHandler(enabled = true) {}
}