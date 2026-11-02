package com.keke125.vaultguard.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

private const val BIOMETRIC_AUTHENTICATORS =
    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL

@Composable
fun LoginScreen(
    context: Context,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    biometricAuthSettingViewModel: BiometricAuthSettingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val activity = LocalActivity.current
    val biometricAuthUiState by biometricAuthSettingViewModel.uiState.collectAsState()
    val biometricStatus = remember {
        BiometricManager.from(context).canAuthenticate(BIOMETRIC_AUTHENTICATORS)
    }
    val canUseBiometric =
        biometricAuthUiState.isBiometricEnabled && biometricStatus == BiometricManager.BIOMETRIC_SUCCESS
    // Only auto-prompt once per screen; afterwards the user re-opens it with the button.
    var hasAutoPrompted by rememberSaveable { mutableStateOf(false) }

    val biometricPrompt = remember(activity) {
        BiometricPrompt(activity as FragmentActivity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val isUserCancel = errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == BiometricPrompt.ERROR_CANCELED
                    if (!isUserCancel) {
                        Toast.makeText(
                            context,
                            String.format(context.getString(R.string.app_biometric_auth_fail1), errString.toString()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    authViewModel.authWithBiometric(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authViewModel.authWithBiometric(true)
                    activity.finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, context.getString(R.string.app_biometric_auth_fail2), Toast.LENGTH_SHORT
                    ).show()
                    authViewModel.authWithBiometric(false)
                }
            })
    }
    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.app_biometric_title))
            .setSubtitle(context.getString(R.string.app_biometric_subtitle))
            .setAllowedAuthenticators(BIOMETRIC_AUTHENTICATORS)
            .build()
    }

    LaunchedEffect(Unit) {
        if (authViewModel.isAuthenticated() && authViewModel.isNotTimeout()) {
            activity?.finish()
        }
    }

    // The biometric preference is loaded asynchronously, so react once it becomes available.
    LaunchedEffect(biometricAuthUiState.isBiometricEnabled) {
        if (!biometricAuthUiState.isBiometricEnabled || hasAutoPrompted) return@LaunchedEffect
        hasAutoPrompted = true
        if (biometricStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        } else if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            Toast.makeText(
                context, context.getString(R.string.app_biometric_not_registered), Toast.LENGTH_LONG
            ).show()
        }
    }

    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            PasswordLoginForm(
                context = context,
                onLogin = { password ->
                    if (authViewModel.checkMainPassword(password)) {
                        Toast.makeText(
                            context, context.getString(R.string.app_login_success), Toast.LENGTH_SHORT
                        ).show()
                        activity?.finish()
                    } else {
                        Toast.makeText(
                            context, context.getString(R.string.app_login_fail), Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onBiometricLogin = if (canUseBiometric) {
                    { biometricPrompt.authenticate(promptInfo) }
                } else {
                    null
                }
            )
        }
    }
    BackHandler(enabled = true) {
        activity?.moveTaskToBack(true)
    }
}

/**
 * Master password form. Shows a biometric login button when [onBiometricLogin] is not null.
 */
@Composable
private fun PasswordLoginForm(
    context: Context,
    onLogin: (String) -> Unit,
    onBiometricLogin: (() -> Unit)?
) {
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
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
            onValueChange = { loginPassword = it },
            label = {
                Text(text = stringResource(id = R.string.app_main_pw))
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "")
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
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
            if (loginPassword.isBlank()) {
                Toast.makeText(
                    context, context.getString(R.string.app_main_pw_required1), Toast.LENGTH_SHORT
                ).show()
            } else {
                onLogin(loginPassword)
            }
        }) {
            Text(stringResource(id = R.string.app_login))
        }
        if (onBiometricLogin != null) {
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            OutlinedButton(onClick = onBiometricLogin) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(id = R.string.app_biometric_login))
            }
        }
    }
}
