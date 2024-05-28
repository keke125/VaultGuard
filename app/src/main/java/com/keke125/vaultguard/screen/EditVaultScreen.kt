package com.keke125.vaultguard.screen

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.EditVaultViewModel
import com.keke125.vaultguard.model.VaultDetails
import com.keke125.vaultguard.navigation.NavigationDestination
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

object EditVaultDestination : NavigationDestination {
    override val route = "edit_vault"
    override val titleRes = R.string.app_edit_vault_title
    const val VAULTID = "vaultId"
    val routeWithArgs = "$route/{$VAULTID}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVaultScreen(
    viewModel: EditVaultViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val deleteVaultsUiState by viewModel.deleteVaultsUiState.collectAsState()
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (isPasswordGeneratorVisible, onPasswordGeneratorVisibleChange) = remember {
                mutableStateOf(false)
            }
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(EditVaultDestination.titleRes))
                }, actions = {
                    TextButton(onClick = {
                        viewModel.updateNameAndUsername(
                            viewModel.vaultUiState.vaultDetails.name,
                            viewModel.vaultUiState.vaultDetails.username
                        )
                        // check fields is not empty
                        if (viewModel.vaultUiState.vaultDetails.name.isEmpty() || viewModel.vaultUiState.vaultDetails.name.isBlank()) {
                            Toast.makeText(
                                context, "請輸入名稱!", Toast.LENGTH_LONG
                            ).show()
                        } else if (viewModel.vaultUiState.vaultDetails.username.isEmpty() || viewModel.vaultUiState.vaultDetails.username.isBlank()) {
                            Toast.makeText(
                                context, "請輸入帳號!", Toast.LENGTH_LONG
                            ).show()
                        } else if (viewModel.vaultUiState.vaultDetails.password.isEmpty() || viewModel.vaultUiState.vaultDetails.password.isBlank()) {
                            Toast.makeText(
                                context, "請輸入密碼!", Toast.LENGTH_LONG
                            ).show()
                        } else if (viewModel.vaultUiState.vaultDetails.urlList.contains("")) {
                            Toast.makeText(
                                context, "請輸入網址!", Toast.LENGTH_LONG
                            ).show()
                        } else if (viewModel.vaultUiState.vaultDetails.totp.isNotEmpty()) {
                            if (viewModel.vaultUiState.vaultDetails.totp.isBlank()) {
                                Toast.makeText(
                                    context, "TOTP驗證碼格式錯誤!", Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            // check if vault already exists
                            if (deleteVaultsUiState.vault != null) {
                                Toast.makeText(
                                    context, "此密碼已儲存於密碼庫!", Toast.LENGTH_LONG
                                ).show()
                            } else {
                                coroutineScope.launch {
                                    viewModel.updateVault()
                                }
                                navController.popBackStack()
                                Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("更新")
                    }
                }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一頁")
                    }
                })
            }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "基本資訊", modifier = Modifier.fillMaxWidth(0.8f), fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.name,
                        onValueChange = {
                            viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(name = it))
                        },
                        singleLine = true,
                        label = { Text("名稱") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.username,
                        onValueChange = {
                            viewModel.updateUiState(
                                viewModel.vaultUiState.vaultDetails.copy(
                                    username = it
                                )
                            )
                        },
                        singleLine = true,
                        label = { Text("帳號") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.password,
                        onValueChange = {
                            viewModel.updateUiState(
                                viewModel.vaultUiState.vaultDetails.copy(
                                    password = it
                                )
                            )
                        },
                        label = { Text("密碼") },
                        leadingIcon = { Icon(Icons.Default.Password, "") },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { onPasswordVisibleChange(!isPasswordVisible) }) {
                                    if (isPasswordVisible) {
                                        Icon(Icons.Default.VisibilityOff, "隱藏密碼")
                                    } else {
                                        Icon(Icons.Default.Visibility, "顯示密碼")
                                    }
                                }
                                IconButton(onClick = {
                                    onPasswordGeneratorVisibleChange(true)
                                }) {
                                    Icon(Icons.Default.Refresh, "產生密碼")
                                }
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.totp,
                        onValueChange = {
                            viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(totp = it))
                        },
                        singleLine = true,
                        label = { Text("TOTP驗證碼") },
                        leadingIcon = { Icon(Icons.Default.Key, null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                copyText(
                                    clipboardManager,
                                    viewModel.vaultUiState.vaultDetails.totp,
                                    context
                                )
                            }) {
                                Icon(Icons.Default.ContentCopy, "產生密碼")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.notes,
                        onValueChange = {
                            viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(notes = it))
                        },
                        label = { Text("備註") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        text = "網址 (URL)",
                        modifier = Modifier.fillMaxWidth(0.8f),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    if (viewModel.vaultUiState.vaultDetails.urlList.isNotEmpty()) {
                        viewModel.vaultUiState.vaultDetails.urlList.forEachIndexed { index, url ->
                            UpdateUrl(url = url, onUrlChange = { newUrl ->
                                val newUrlList =
                                    viewModel.vaultUiState.vaultDetails.urlList.toMutableList()
                                newUrlList[index] = newUrl
                                viewModel.updateUiState(
                                    viewModel.vaultUiState.vaultDetails.copy(
                                        urlList = newUrlList
                                    )
                                )
                            }, onDelete = {
                                val newUrlList =
                                    viewModel.vaultUiState.vaultDetails.urlList.toMutableList()
                                newUrlList.removeAt(index)
                                viewModel.updateUiState(
                                    viewModel.vaultUiState.vaultDetails.copy(
                                        urlList = newUrlList
                                    )
                                )
                            })
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    Button(onClick = {
                        val newUrlList = viewModel.vaultUiState.vaultDetails.urlList.toMutableList()
                        newUrlList.add("")
                        viewModel.updateUiState(
                            viewModel.vaultUiState.vaultDetails.copy(
                                urlList = newUrlList
                            )
                        )
                    }) {
                        Text("新增網址")
                    }
                }
                when {
                    isPasswordGeneratorVisible -> {
                        PasswordGeneratorDialogConfirm(
                            onDismissRequest = { onPasswordGeneratorVisibleChange(false) },
                            context,
                            { viewModel.updateUiState(it) },
                            viewModel.vaultUiState.vaultDetails
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordGeneratorDialogConfirm(
    onDismissRequest: () -> Unit,
    context: Context,
    onVaultDetailsChange: (VaultDetails) -> Unit,
    vaultDetails: VaultDetails
) {
    val (length, onLengthChange) = remember {
        mutableFloatStateOf(16f)
    }
    val (isUpper, onUpperChange) = remember {
        mutableStateOf(true)
    }
    val (isLower, onLowerChange) = remember {
        mutableStateOf(true)
    }
    val (isNumber, onNumberChange) = remember {
        mutableStateOf(true)
    }
    val (isSpecialChar, onSpecialCharChange) = remember {
        mutableStateOf(true)
    }
    val (password, onPasswordChange) = remember {
        mutableStateOf(
            generatePassword(
                length.toInt(), isUpper, isLower, isNumber, isSpecialChar, context
            )
        )
    }
    val (confirm, onConfirmChange) = remember {
        mutableStateOf(false)
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .padding(top = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    SelectionContainer {
                        Text(
                            text = password,
                            modifier = Modifier.width(224.dp),
                            fontSize = 16.sp,
                        )
                    }
                    IconButton(onClick = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, isLower, isNumber, isSpecialChar, context
                            )
                        )
                    }) {
                        Icon(Icons.Default.Refresh, "重新產生密碼")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = ContextCompat.getString(
                            context, R.string.app_length
                        ), fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = length.toInt().toString(), fontSize = 20.sp)
                    Slider(
                        value = length,
                        onValueChange = { onLengthChange(it) },
                        onValueChangeFinished = {
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(),
                                    isUpper,
                                    isLower,
                                    isNumber,
                                    isSpecialChar,
                                    context
                                )
                            )
                        },
                        valueRange = 3f..128f,
                        modifier = Modifier.width(128.dp)
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isUpper, onValueChange = {
                            onUpperChange(!isUpper)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(), it, isLower, isNumber, isSpecialChar, context
                                )
                            )
                        }, role = Role.Checkbox
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = isUpper, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), it, isLower, isNumber, isSpecialChar, context
                            )
                        )
                        onUpperChange(!isUpper)
                    })
                    Text(
                        text = ContextCompat.getString(
                            context, R.string.app_upper
                        ), fontSize = 18.sp
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isLower,
                        onValueChange = {
                            onLowerChange(!isLower)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(), isUpper, it, isNumber, isSpecialChar, context
                                )
                            )
                        },
                        role = Role.Checkbox,
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isLower, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, it, isNumber, isSpecialChar, context
                            )
                        )
                        onLowerChange(!isLower)
                    })
                    Text(
                        text = ContextCompat.getString(
                            context, R.string.app_lower
                        ), fontSize = 20.sp
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isNumber, onValueChange = {
                            onNumberChange(!isNumber)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(), isUpper, isLower, it, isSpecialChar, context
                                )
                            )
                        }, role = Role.Checkbox
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isNumber, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, isLower, it, isSpecialChar, context
                            )
                        )
                        onNumberChange(!isNumber)
                    })
                    Text(
                        text = ContextCompat.getString(
                            context, R.string.app_number
                        ), fontSize = 20.sp
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isSpecialChar, onValueChange = {
                            onSpecialCharChange(!isSpecialChar)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(), isUpper, isLower, isNumber, it, context
                                )
                            )
                        }, role = Role.Checkbox
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSpecialChar, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, isLower, isNumber, it, context
                            )
                        )
                        onSpecialCharChange(!isSpecialChar)
                    })
                    Text(
                        text = ContextCompat.getString(
                            context, R.string.app_special_char
                        ), fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Row {
                    TextButton(onClick = {
                        onConfirmChange(true)
                    }) {
                        Text(text = "選擇", fontSize = 20.sp)
                    }
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = "取消", fontSize = 20.sp)
                    }
                }
            }
        }
    }
    when {
        confirm -> {
            UpdatePasswordConfirm(onConfirmDismissRequest = { onConfirmChange(false) },
                onPasswordGeneratorDialogDismissRequest = { onDismissRequest() },
                password,
                { onVaultDetailsChange(vaultDetails.copy(password = password)) })
        }
    }
}

@Composable
fun UpdatePasswordConfirm(
    onConfirmDismissRequest: () -> Unit,
    onPasswordGeneratorDialogDismissRequest: () -> Unit,
    password: String,
    onRealPasswordChange: (String) -> Unit
) {
    AlertDialog(icon = {
        Icon(Icons.Default.Warning, "警告")
    }, title = {
        Text(text = "是否要更新密碼?")
    }, text = {
        Text(text = "當前密碼將被取代")
    }, onDismissRequest = {
        onConfirmDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmDismissRequest()
            onPasswordGeneratorDialogDismissRequest()
        }) {
            Text("取消")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onRealPasswordChange(password)
            onConfirmDismissRequest()
            onPasswordGeneratorDialogDismissRequest()
        }) {
            Text("確定")
        }
    })

}