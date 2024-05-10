package com.keke125.vaultguard.screen

import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.VaultDetailsViewModel
import com.keke125.vaultguard.navigation.NavigationDestination
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

object VaultDetailsDestination : NavigationDestination {
    override val route = "vault_details"
    override val titleRes = R.string.app_vault_details_title
    const val VAULTED = "vaultId"
    val routeWithArgs = "$route/{$VAULTED}"
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
            val (isPasswordDeleteRequired, onPasswordDeleteRequiredChange) = remember {
                mutableStateOf(false)
            }
            val (isMoreOptionsExpanded, onMoreOptionsExpandedChange) = remember {
                mutableStateOf(false)
            }
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(VaultDetailsDestination.titleRes))
                }, actions = {
                    IconButton(onClick = { onMoreOptionsExpandedChange(true) }) {
                        Icon(imageVector = Icons.Filled.MoreHoriz, contentDescription = "")
                    }
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
                        navController.navigate("${EditVaultDestination.route}/${uiState.value.vaultDetails.uid}")
                    },
                ) {
                    Icon(Icons.Filled.Edit, "")
                }
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
                    Spacer(modifier = Modifier.padding(vertical = 16.dp))
                    if (uiState.value.vaultDetails.urlList.isNotEmpty()) {
                        Text(
                            text = "網址 (URL)",
                            modifier = Modifier.fillMaxWidth(0.8f),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.padding(vertical = 4.dp))
                        uiState.value.vaultDetails.urlList.forEachIndexed { index, url ->
                            Url(url = url, context = context)
                        }
                    }
                    MoreOptionsDialog(
                        expanded = isMoreOptionsExpanded,
                        onExpandedChange = onMoreOptionsExpandedChange,
                        onPasswordDeleteRequired = onPasswordDeleteRequiredChange
                    )
                    when {
                        isPasswordDeleteRequired -> {
                            DeletePasswordConfirm(onPasswordDeleteRequiredChange, onDeleted = {
                                coroutineScope.launch {
                                    viewModel.deleteItem()
                                }
                                navController.popBackStack()
                                Toast.makeText(context, "密碼已被刪除", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeletePasswordConfirm(
    onPasswordDeleteRequiredChange: (Boolean) -> Unit,
    onDeleted: () -> Unit,
) {
    AlertDialog(icon = {
        Icon(Icons.Default.Warning, "")
    }, title = {
        Text(text = "是否要刪除密碼?")
    }, text = {
        Text(text = "當前密碼將被刪除")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsDialog(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onPasswordDeleteRequired: (Boolean) -> Unit,
) {
    when {
        expanded -> {
            BasicAlertDialog(onDismissRequest = { onExpandedChange(false) }) {
                Column {
                    ListItem(headlineContent = { Text("刪除密碼") }, leadingContent = {
                        Icon(
                            Icons.Outlined.Delete, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        onExpandedChange(false)
                        onPasswordDeleteRequired(true)
                    })
                    ListItem(headlineContent = { }, trailingContent = {
                        TextButton(onClick = { onExpandedChange(false) }) {
                            Text("Cancel", color = ListItemDefaults.colors().headlineColor)
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun Url(url: String, context: Context) {
    OutlinedTextField(
        value = url,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text("網址") },
        leadingIcon = { Icon(Icons.Default.Link, "") },
        trailingIcon = {
            IconButton(onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_VIEW
                        val uri = Uri.parse(url)
                        setData(uri)
                }

                try {
                    startActivity(context, sendIntent, null)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "網址錯誤!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, "")
            }
        },
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}