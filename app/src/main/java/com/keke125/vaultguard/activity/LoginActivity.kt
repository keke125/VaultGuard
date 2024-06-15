package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.AuthViewModel
import com.keke125.vaultguard.model.BiometricAuthSettingViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(this)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    context: Context,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    biometricAuthSettingViewModel: BiometricAuthSettingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val activity = context as Activity
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (loginPassword, onLoginPasswordChange) = remember { mutableStateOf("") }
            val (biometricAuthCancel, onBiometricAuthCancel) = remember {
                mutableStateOf(false)
            }
            val biometricAuthUiState by biometricAuthSettingViewModel.uiState.asLiveData()
                .observeAsState()
            val biometricManager = BiometricManager.from(context)
            if (authViewModel.isAuthenticated() && authViewModel.isNotTimeout()) {
                activity.finish()
            } else {
                if (biometricAuthUiState != null) {
                    if (biometricAuthUiState!!.isBiometricEnabled and !biometricAuthCancel) {
                        biometricAuthSettingViewModel.updateCanAuthenticateWithBiometrics(
                            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                                BiometricManager.BIOMETRIC_SUCCESS -> {
                                    biometricAuthSettingViewModel.updateBiometricEnabled(true)
                                    true
                                }

                                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                                    false
                                }

                                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                    false
                                }

                                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                    Toast.makeText(
                                        context, context.getString(R.string.app_biometric_not_registered), Toast.LENGTH_LONG
                                    ).show()
                                    false
                                }

                                else -> {
                                    false
                                }
                            }
                        )
                        if (biometricAuthUiState!!.canAuthenticateWithBiometrics) {
                            BiometricScreen(context, onBiometricAuthCancel)
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.ic_launcher),
                                    contentDescription = "",
                                    modifier = Modifier.size(128.dp)
                                )
                                Text(text = stringResource(id = R.string.app_login), fontSize = 32.sp)
                                OutlinedTextField(
                                    value = loginPassword,
                                    onValueChange = { onLoginPasswordChange(it) },
                                    label = {
                                        Text(
                                            text = stringResource(id = R.string.app_main_pw)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = ""
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
                                    placeholder = { Text(text = stringResource(id = R.string.app_main_pw_required2)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    visualTransformation = if (isPasswordVisible) {
                                        VisualTransformation.None
                                    } else {
                                        PasswordVisualTransformation()
                                    }
                                )
                                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                                Button(onClick = {
                                    if (loginPassword.isEmpty() || loginPassword.isBlank()) {
                                        Toast.makeText(
                                            context, context.getString(R.string.app_main_pw_required1), Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (authViewModel.checkMainPassword(
                                            loginPassword
                                        )
                                    ) {
                                        Toast.makeText(
                                            context, context.getString(R.string.app_login_success), Toast.LENGTH_SHORT
                                        ).show()
                                        activity.finish()
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.app_login_fail), Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }) {
                                    Text(stringResource(id = R.string.app_login))
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_launcher),
                                contentDescription = "",
                                modifier = Modifier.size(128.dp)
                            )
                            Text(text = stringResource(id = R.string.app_login), fontSize = 32.sp)
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { onLoginPasswordChange(it) },
                                label = {
                                    Text(
                                        text = stringResource(id = R.string.app_main_pw)
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
                                placeholder = { Text(text = stringResource(id = R.string.app_main_pw_required2)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = if (isPasswordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                }
                            )
                            Spacer(modifier = Modifier.padding(vertical = 8.dp))
                            Button(onClick = {
                                if (loginPassword.isEmpty() || loginPassword.isBlank()) {
                                    Toast.makeText(
                                        context, context.getString(R.string.app_main_pw_required1), Toast.LENGTH_SHORT
                                    ).show()
                                } else if (authViewModel.checkMainPassword(
                                        loginPassword
                                    )
                                ) {
                                    Toast.makeText(
                                        context, context.getString(R.string.app_login_success), Toast.LENGTH_SHORT
                                    ).show()
                                    activity.finish()
                                } else {
                                    Toast.makeText(context, context.getString(R.string.app_login_fail), Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text(stringResource(id = R.string.app_login))
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_launcher),
                            contentDescription = "",
                            modifier = Modifier.size(128.dp)
                        )
                        Text(text = stringResource(id = R.string.app_login), fontSize = 32.sp)
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { onLoginPasswordChange(it) },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.app_main_pw)
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
                            placeholder = { Text(text = stringResource(id = R.string.app_main_pw_required2)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                        Button(onClick = {
                            if (loginPassword.isEmpty() || loginPassword.isBlank()) {
                                Toast.makeText(
                                    context, context.getString(R.string.app_main_pw_required1), Toast.LENGTH_SHORT
                                ).show()
                            } else if (authViewModel.checkMainPassword(
                                    loginPassword
                                )
                            ) {
                                Toast.makeText(
                                    context, context.getString(R.string.app_login_success), Toast.LENGTH_SHORT
                                ).show()
                                activity.finish()
                            } else {
                                Toast.makeText(context, context.getString(R.string.app_login_fail), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(stringResource(id = R.string.app_login))
                        }
                    }
                }
            }
        }
    }
    BackHandler(enabled = true) {
        val activity = context as Activity
        activity.moveTaskToBack(true)
    }
}

@Composable
fun BiometricScreen(
    context: Context,
    onBiometricAuthCancel: (Boolean) -> Unit,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val (isTryAuth, onTryAuth) = remember { mutableStateOf(false) }
    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(context as FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int, errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(
                    context, context.getString(R.string.app_biometric_auth_fail1), Toast.LENGTH_SHORT
                ).show()
                onBiometricAuthCancel(true)
                authViewModel.authWithBiometric(false)
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onTryAuth(true)
                authViewModel.authWithBiometric(true)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    context, context.getString(R.string.app_biometric_auth_fail2), Toast.LENGTH_SHORT
                ).show()
                authViewModel.authWithBiometric(false)
            }
        })
    val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(context.getString(R.string.app_biometric_title))
        .setSubtitle(context.getString(R.string.app_biometric_subtitle))
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .build()
    if (isTryAuth) {
        context.finish()
    } else {
        biometricPrompt.authenticate(promptInfo)
    }
}