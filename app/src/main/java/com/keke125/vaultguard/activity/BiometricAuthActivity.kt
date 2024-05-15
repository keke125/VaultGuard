package com.keke125.vaultguard.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

class BiometricAuthActivity : AppCompatActivity() {

    private var canAuthenticateWithBiometrics by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ), title = { Text("生物辨識") })
                    }) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .padding(innerPadding)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val biometricManager = BiometricManager.from(this@BiometricAuthActivity)
                            canAuthenticateWithBiometrics =
                                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                                    BiometricManager.BIOMETRIC_SUCCESS -> {
                                        Log.d(
                                            "MY_APP_TAG", "App can authenticate using biometrics."
                                        )
                                        true
                                    }

                                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                                        Log.e(
                                            "MY_APP_TAG",
                                            "No biometric features available on this device."
                                        )
                                        false
                                    }

                                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                        Log.e(
                                            "MY_APP_TAG",
                                            "Biometric features are currently unavailable."
                                        )
                                        false
                                    }

                                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                        val enrollIntent: Intent =
                                            if (Build.VERSION.SDK_INT >= 30) {
                                                Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                                            } else {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                                                } else {
                                                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                                                }
                                            }
                                        startActivityForResult(
                                            this@BiometricAuthActivity as FragmentActivity,
                                            enrollIntent,
                                            100,
                                            null
                                        )
                                        false
                                    }

                                    else -> {
                                        false
                                    }
                                }
                            if (canAuthenticateWithBiometrics) {
                                val executor =
                                    ContextCompat.getMainExecutor(this@BiometricAuthActivity)
                                val biometricPrompt =
                                    BiometricPrompt(this@BiometricAuthActivity as FragmentActivity,
                                        executor,
                                        object : BiometricPrompt.AuthenticationCallback() {
                                            override fun onAuthenticationError(
                                                errorCode: Int, errString: CharSequence
                                            ) {
                                                super.onAuthenticationError(errorCode, errString)
                                                Toast.makeText(
                                                    this@BiometricAuthActivity,
                                                    "Authentication error: $errString",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            override fun onAuthenticationSucceeded(
                                                result: BiometricPrompt.AuthenticationResult
                                            ) {
                                                super.onAuthenticationSucceeded(result)
                                                Toast.makeText(
                                                    this@BiometricAuthActivity,
                                                    "Authentication succeeded!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            override fun onAuthenticationFailed() {
                                                super.onAuthenticationFailed()
                                                Toast.makeText(
                                                    this@BiometricAuthActivity,
                                                    "Authentication failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })

                                val promptInfo =
                                    BiometricPrompt.PromptInfo.Builder().setTitle("登入Vault Guard")
                                        .setSubtitle("使用生物辨識登入Vault Guard")
                                        .setNegativeButtonText("取消").build()

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
        }
    }

    @Deprecated(
        "This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.",
        ReplaceWith(
            "super.onActivityResult()", "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val biometricManager = BiometricManager.from(this)
            canAuthenticateWithBiometrics =
                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        true
                    }

                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        false
                    }

                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        false
                    }

                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        false
                    }

                    else -> {
                        false
                    }
                }
        }
    }

}
