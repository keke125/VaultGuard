package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.DeleteVaultsViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch
import com.keke125.vaultguard.R

class DeleteVaultsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    DeleteVaultsScreen(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteVaultsScreen(
    context: Context,
    viewModel: DeleteVaultsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val activity = LocalContext.current as? Activity
    val coroutineScope = rememberCoroutineScope()
    val deleteVaultsUiState by viewModel.vaultUiState.collectAsState()
    val deleteFoldersUiState by viewModel.folderUiState.collectAsState()
    val (isPasswordVisible, onPasswordVisibleChange) = remember { mutableStateOf(false) }
    val (isDeleteConfirmExpanded, onDeleteConfirmExpandedChange) = remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text(stringResource(id = R.string.app_clear_vault_repo)) }, navigationIcon = {
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
                .padding(innerPadding)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = viewModel.deleteVaultsUiState.deleteVaults.password,
                onValueChange = {
                    viewModel.updateUiState(
                        viewModel.deleteVaultsUiState.deleteVaults.copy(
                            password = it
                        )
                    )
                },
                label = { Text(stringResource(id = R.string.app_main_pw)) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Password, null) },
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
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
            Button(onClick = {
                if (viewModel.deleteVaultsUiState.deleteVaults.password.isEmpty() || viewModel.deleteVaultsUiState.deleteVaults.password.isBlank()) {
                    Toast.makeText(context, context.getString(R.string.app_main_pw_required), Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (viewModel.checkMainPassword(viewModel.deleteVaultsUiState.deleteVaults.password)) {
                    onDeleteConfirmExpandedChange(true)
                } else {
                    Toast.makeText(context, context.getString(R.string.app_main_pw_error), Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = stringResource(id = R.string.app_clear))
            }
            when {
                isDeleteConfirmExpanded -> {
                    DeleteVaultsConfirm(onDeleteConfirmExpandedChange, onDeleted = {
                        if (deleteVaultsUiState.vaultList.isNotEmpty() || deleteFoldersUiState.folderList.isNotEmpty()) {
                            coroutineScope.launch {
                                viewModel.deleteVaultsAndFolders(
                                    deleteVaultsUiState.vaultList, deleteFoldersUiState.folderList
                                )
                            }
                            viewModel.updateUiState(
                                viewModel.deleteVaultsUiState.deleteVaults.copy(
                                    password = ""
                                )
                            )
                            Toast.makeText(context, context.getString(R.string.app_clear_vault_repo_success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, context.getString(R.string.app_clear_vault_repo_fail), Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun DeleteVaultsConfirm(
    onPasswordDeleteRequiredChange: (Boolean) -> Unit,
    onDeleted: () -> Unit,
) {
    AlertDialog(icon = {
        Icon(Icons.Default.Warning, stringResource(id = R.string.app_warn))
    }, title = {
        Text(text = stringResource(id = R.string.app_ask_clear))
    }, text = {
        Text(text = stringResource(id = R.string.app_ask_clear_des))
    }, onDismissRequest = {
        onPasswordDeleteRequiredChange(false)
    }, confirmButton = {
        TextButton(onClick = {
            onDeleted()
            onPasswordDeleteRequiredChange(false)
        }) {
            Text(stringResource(id = R.string.app_confirm))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onPasswordDeleteRequiredChange(false)
        }) {
            Text(stringResource(id = R.string.app_cancel))
        }
    })
}