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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.DeleteVaultsUiState
import com.keke125.vaultguard.model.DeleteVaultsViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

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
    viewModel: DeleteVaultsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    changeMainPasswordUiState: DeleteVaultsUiState = viewModel.deleteVaultsUiState
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
        ), title = { Text("清空密碼庫") }, navigationIcon = {
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
                value = changeMainPasswordUiState.deleteVaults.password,
                onValueChange = {
                    viewModel.updateUiState(
                        changeMainPasswordUiState.deleteVaults.copy(
                            password = it
                        )
                    )
                },
                label = { Text("主密碼") },
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
                if (changeMainPasswordUiState.deleteVaults.password.isEmpty() || changeMainPasswordUiState.deleteVaults.password.isBlank()) {
                    Toast.makeText(context, "請輸入主密碼", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (viewModel.checkMainPassword(changeMainPasswordUiState.deleteVaults.password)) {
                    onDeleteConfirmExpandedChange(true)
                } else {
                    Toast.makeText(context, "主密碼錯誤!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "清空")
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
                                changeMainPasswordUiState.deleteVaults.copy(
                                    password = ""
                                )
                            )
                            Toast.makeText(context, "清空成功", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "尚未儲存密碼或資料夾!", Toast.LENGTH_SHORT)
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
        Icon(Icons.Default.Warning, "警告")
    }, title = {
        Text(text = "是否要清空密碼庫?")
    }, text = {
        Text(text = "所有密碼及資料夾將被刪除!")
    }, onDismissRequest = {
        onPasswordDeleteRequiredChange(false)
    }, confirmButton = {
        TextButton(onClick = {
            onPasswordDeleteRequiredChange(false)
        }) {
            Text("取消")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDeleted()
            onPasswordDeleteRequiredChange(false)
        }) {
            Text("確定")
        }
    })
}