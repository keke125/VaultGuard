package com.keke125.vaultguard.screen

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.navigation.NavigationDestination
import com.keke125.vaultguard.R
import com.keke125.vaultguard.activity.EditVaultActivity
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.VaultDetailsViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

object VaultDetailsDestination : NavigationDestination {
    override val route = "vault_details"
    override val titleRes = R.string.app_pg_title
    const val vaultIdArg = "vaultId"
    val routeWithArgs = "$route/{$vaultIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultDetailsScreen(
    navController: NavController,
    viewModel: VaultDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background

        ) {
            val context = navController.context
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val uiState = viewModel.uiState.collectAsState()
            val coroutineScope = rememberCoroutineScope()
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text("檢視密碼")
                }, actions = {
                    /*TODO*/
                }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                })
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent()
                        intent.setClass(context, EditVaultActivity::class.java)
                        //intent.putExtra("vault", vault)
                        startActivity(context, intent, null)
                    },
                ) {
                    Icon(Icons.Filled.Edit, "")
                }
            }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.name,
                        onValueChange = {},
                        singleLine = true,
                        readOnly = true,
                        label = { Text("名稱") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.username,
                        onValueChange = {},
                        singleLine = true,
                        readOnly = true,
                        label = { Text("帳號") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                        trailingIcon = {
                            IconButton(onClick = {
                                copyUsername(
                                    clipboardManager, uiState.value.vaultDetails.username, context
                                )
                            }) {
                                Icon(Icons.Default.ContentCopy, "")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.password,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("密碼") },
                        leadingIcon = { Icon(Icons.Default.Password, "") },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { onPasswordVisibleChange(!isPasswordVisible) }) {
                                    if (isPasswordVisible) {
                                        Icon(Icons.Default.VisibilityOff, "")
                                    } else {
                                        Icon(Icons.Default.Visibility, "")
                                    }
                                }
                                IconButton(onClick = {
                                    copyPassword(
                                        clipboardManager,
                                        uiState.value.vaultDetails.password,
                                        context
                                    )
                                }) {
                                    Icon(Icons.Default.ContentCopy, "")
                                }
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        }
    }
}

/*
fun copyUsername(clipboardManager: ClipboardManager, username: String, context: Context) {
    // When setting the clipboard text.
    clipboardManager.setPrimaryClip(ClipData.newPlainText("username", username))
    // Only show a toast for Android 12 and lower.
    //if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
    Toast.makeText(
        context, "Copied", Toast.LENGTH_SHORT
    ).show()
}

fun copyPassword(clipboardManager: ClipboardManager, password: String, context: Context) {
    val clipData = ClipData.newPlainText("password", password)
    clipData.apply {
        description.extras = PersistableBundle().apply {
            putBoolean("android.content.extra.IS_SENSITIVE", true)
        }
    }
    // When setting the clipboard text.
    clipboardManager.setPrimaryClip(clipData)
    // Only show a toast for Android 12 and lower.
    // if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
    Toast.makeText(
        context, "Copied", Toast.LENGTH_SHORT
    ).show()
}
*/