package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.BiometricAuthSettingViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

class AuthSettingActivity : AppCompatActivity() {

    private val biometricAuthSettingViewModel: BiometricAuthSettingViewModel by viewModels(
        factoryProducer = { AppViewModelProvider.Factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BiometricAuthSettingScreen(this, biometricAuthSettingViewModel)
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
            biometricAuthSettingViewModel.updateCanAuthenticateWithBiometrics(
                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)) {
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
                        Toast.makeText(this, getString(R.string.app_biometric_not_registered), Toast.LENGTH_LONG).show()
                        false
                    }

                    else -> {
                        false
                    }
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricAuthSettingScreen(context: Context, viewModel: BiometricAuthSettingViewModel) {
    val activity = context as? Activity
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text(stringResource(id = R.string.app_security_setting)) }, navigationIcon = {
            IconButton(onClick = {
                activity?.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val biometricManager = BiometricManager.from(context)
            val uiState = viewModel.uiState.collectAsState()
            val (isPromptBiometricEnabled, onPromptBiometricEnabledChange) = remember {
                mutableStateOf(false)
            }
            val enrollIntent: Intent = if (Build.VERSION.SDK_INT >= 30) {
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL or BIOMETRIC_WEAK
                    )
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                } else {
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                }
            }
            ListItem(headlineContent = { Text(stringResource(id = R.string.app_change_main_pw)) },
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, ChangeMainPasswordActivity::class.java))
                }
            )
            HorizontalDivider()
            ListItem(headlineContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = uiState.value.isBiometricEnabled, onCheckedChange = {
                        if (it) {
                            viewModel.updateCanAuthenticateWithBiometrics(
                                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL or BIOMETRIC_WEAK)) {
                                    BiometricManager.BIOMETRIC_SUCCESS -> {
                                        Log.d(
                                            "VaultGuard", "App can authenticate using biometrics."
                                        )
                                        viewModel.updateBiometricEnabled(true)
                                        true
                                    }

                                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                                        Log.e(
                                            "VaultGuard",
                                            "No biometric features available on this device."
                                        )
                                        Toast.makeText(
                                            context, context.getString(R.string.app_biometric_unavailable), Toast.LENGTH_LONG
                                        ).show()
                                        viewModel.updateBiometricEnabled(false)
                                        false
                                    }

                                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                        Log.e(
                                            "VaultGuard",
                                            "Biometric features are currently unavailable."
                                        )
                                        Toast.makeText(
                                            context, context.getString(R.string.app_biometric_temp_unavailable), Toast.LENGTH_LONG
                                        ).show()
                                        viewModel.updateBiometricEnabled(false)
                                        false
                                    }

                                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                        onPromptBiometricEnabledChange(true)
                                        false
                                    }

                                    else -> {
                                        Toast.makeText(context, context.getString(R.string.app_biometric_error), Toast.LENGTH_LONG)
                                            .show()
                                        false
                                    }
                                }
                            )
                        } else {
                            viewModel.updateBiometricEnabled(false)
                        }
                    })
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = stringResource(id = R.string.app_use_biometric))
                }
            })
            HorizontalDivider()
            when {
                isPromptBiometricEnabled -> {
                    PromptBiometricDialogConfirm(onDismissRequest = {
                        onPromptBiometricEnabledChange(
                            false
                        )
                    }, onPromptBiometricResultChange = {
                        startActivityForResult(
                            context as FragmentActivity, enrollIntent, 100, null
                        )
                    }, context = context)
                }
            }
        }
    }
}

@Composable
fun PromptBiometricDialogConfirm(
    onDismissRequest: () -> Unit, onPromptBiometricResultChange: () -> Unit, context: Context
) {
    AlertDialog(icon = {
        Icon(Icons.Default.Info, stringResource(id = R.string.app_info))
    }, title = {
        Text(text = stringResource(id = R.string.app_ask_biometric))
    }, text = {
        Text(text = stringResource(id = R.string.app_ask_biometric_des))
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onPromptBiometricResultChange()
            onDismissRequest()
        }) {
            Text(stringResource(id = R.string.app_confirm))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
            Toast.makeText(context, context.getString(R.string.app_biometric_not_registered), Toast.LENGTH_LONG).show()
        }) {
            Text(stringResource(id = R.string.app_cancel))
        }
    })

}