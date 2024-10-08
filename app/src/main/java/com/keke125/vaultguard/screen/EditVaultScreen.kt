package com.keke125.vaultguard.screen

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.keke125.vaultguard.activity.BarcodeScannerActivity
import com.keke125.vaultguard.activity.getSecretFromUri
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.EditVaultViewModel
import com.keke125.vaultguard.model.VaultDetails
import com.keke125.vaultguard.navigation.NavigationDestination
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

object EditVaultDestination : NavigationDestination {
    override val route = "edit_vault"
    override val titleRes = R.string.app_edit_password_title
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
            val foldersUiState by viewModel.foldersUiState.collectAsState()
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (isPasswordGeneratorVisible, onPasswordGeneratorVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (isDropdownExpanded, onDropdownExpandedChange) = remember { mutableStateOf(false) }
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val openBarcodeScannerLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val url = result.data
                    if (url != null) {
                        if (url.getStringExtra("url") != null) {
                            if (getSecretFromUri(Uri.parse(url.getStringExtra("url"))) != null) {
                                viewModel.updateUiState(
                                    viewModel.vaultUiState.vaultDetails.copy(
                                        totp = getSecretFromUri(Uri.parse(url.getStringExtra("url")))!!
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.app_scan_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.app_totp_error1),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.app_totp_error1),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.app_totp_error2), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.app_scan_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(EditVaultDestination.titleRes))
                }, actions = {
                    TextButton(onClick = {
                        if (checkVault(
                                viewModel.vaultUiState.vaultDetails.name,
                                viewModel.vaultUiState.vaultDetails.username,
                                viewModel.vaultUiState.vaultDetails.urlList,
                                viewModel.vaultUiState.vaultDetails.totp,
                                context
                            )
                        ) {
                            coroutineScope.launch {
                                viewModel.updateVault()
                            }
                            navController.popBackStack()
                            Toast.makeText(context, context.getString(R.string.app_update_success), Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(stringResource(id = R.string.app_update))
                    }
                }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, context.getString(R.string.app_back_prev_screen))
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
                        text = stringResource(id = R.string.app_basic_info), modifier = Modifier.fillMaxWidth(0.8f), fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.name,
                        onValueChange = {
                            viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(name = it))
                        },
                        singleLine = true,
                        label = { Text(stringResource(id = R.string.app_password_name1)) },
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
                        label = { Text(stringResource(id = R.string.app_password_username1)) },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
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
                        label = { Text(stringResource(id = R.string.app_password_pw)) },
                        leadingIcon = { Icon(Icons.Default.Password, null) },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { onPasswordVisibleChange(!isPasswordVisible) }) {
                                    if (isPasswordVisible) {
                                        Icon(Icons.Default.VisibilityOff, context.getString(R.string.app_hide_pw))
                                    } else {
                                        Icon(Icons.Default.Visibility, context.getString(R.string.app_show_pw))
                                    }
                                }
                                IconButton(onClick = {
                                    onPasswordGeneratorVisibleChange(true)
                                }) {
                                    Icon(Icons.Default.Refresh, context.getString(R.string.app_generate_pw))
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
                        label = { Text(stringResource(id = R.string.app_totp)) },
                        leadingIcon = { Icon(Icons.Default.Key, null) },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = {
                                    if(context.packageManager.hasSystemFeature("android.hardware.camera.any") && context.packageManager.hasSystemFeature("android.hardware.camera")){
                                        val intent = Intent(context, BarcodeScannerActivity::class.java)
                                        openBarcodeScannerLauncher.launch(intent)
                                    }else{
                                        Toast.makeText(context, context.getString(R.string.app_scan_unavailable), Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Icon(Icons.Default.QrCodeScanner, context.getString(R.string.app_scan_qrcode))
                                }
                                IconButton(onClick = {
                                    copyText(
                                        clipboardManager,
                                        viewModel.vaultUiState.vaultDetails.totp,
                                        context
                                    )
                                }) {
                                    Icon(Icons.Default.ContentCopy, context.getString(R.string.app_copy_totp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { onDropdownExpandedChange(!isDropdownExpanded) },
                    ) {
                        OutlinedTextField(
                            value = viewModel.folderName.value ?: stringResource(id = R.string.app_uncategorized),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.app_folder)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(0.8f)
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { onDropdownExpandedChange(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.app_uncategorized)) },
                                onClick = {
                                    viewModel.updateFolderName(context.getString(R.string.app_uncategorized))
                                    onDropdownExpandedChange(false)
                                    viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(folderUid = null))
                                }
                            )
                            foldersUiState.folderList.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder.name) },
                                    onClick = {
                                        viewModel.updateFolderName(folder.name)
                                        onDropdownExpandedChange(false)
                                        viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(folderUid = folder.uid))
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = viewModel.vaultUiState.vaultDetails.notes,
                        onValueChange = {
                            viewModel.updateUiState(viewModel.vaultUiState.vaultDetails.copy(notes = it))
                        },
                        label = { Text(stringResource(id = R.string.app_note)) },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        text = stringResource(id = R.string.app_url),
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
                        Text(stringResource(id = R.string.app_add_url))
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
                        Icon(Icons.Default.Refresh, context.getString(R.string.app_regenerate_password))
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
                        Text(text = stringResource(id = R.string.app_select), fontSize = 20.sp)
                    }
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = stringResource(id = R.string.app_cancel), fontSize = 20.sp)
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
        Icon(Icons.Default.Warning, stringResource(id = R.string.app_warn))
    }, title = {
        Text(text = stringResource(id = R.string.app_ask_update_pw))
    }, text = {
        Text(text = stringResource(id = R.string.app_ask_update_pw_desc))
    }, onDismissRequest = {
        onConfirmDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onRealPasswordChange(password)
            onConfirmDismissRequest()
            onPasswordGeneratorDialogDismissRequest()
        }) {
            Text(stringResource(id = R.string.app_confirm))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onConfirmDismissRequest()
            onPasswordGeneratorDialogDismissRequest()
        }) {
            Text(stringResource(id = R.string.app_cancel))
        }
    })

}