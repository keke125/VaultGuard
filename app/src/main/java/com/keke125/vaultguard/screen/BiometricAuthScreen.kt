package com.keke125.vaultguard.screen

import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

@Composable
fun BiometricAuthScreen(
    navController: NavController
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val biometricManager = BiometricManager.from(context)
            val canAuthenticateWithBiometrics: Boolean =
                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                        true
                    }

                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        Log.e("MY_APP_TAG", "No biometric features available on this device.")
                        false
                    }

                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                        false
                    }

                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {/*
                        // Prompts the user to create credentials that your app accepts.
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                        }
                        startActivityForResult(enrollIntent, 100)
                        */
                        false
                    }

                    else -> {
                        false
                    }
                }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (canAuthenticateWithBiometrics) {
                    val executor = ContextCompat.getMainExecutor(context)
                    val biometricPrompt = BiometricPrompt(context as FragmentActivity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int, errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                Toast.makeText(
                                    context, "Authentication error: $errString", Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult
                            ) {
                                super.onAuthenticationSucceeded(result)
                                Toast.makeText(
                                    context, "Authentication succeeded!", Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                Toast.makeText(
                                    context, "Authentication failed", Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                    val promptInfo =
                        BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login for my app")
                            .setSubtitle("Log in using your biometric credential")
                            .setNegativeButtonText("Use account password").build()

                    Button(onClick = { biometricPrompt.authenticate(promptInfo) }) {
                        Text(text = "生物辨識")
                    }
                } else {
                    Text(text = "您的裝置不支援生物辨識")
                }
            }
        }
    }
}