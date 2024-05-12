package com.keke125.vaultguard.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.PersistableBundle
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.VaultViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    navController: NavController,
    navigateToViewVault: (Int) -> Unit,
    navigateToEditVault: (Int) -> Unit,
    navigateToSearchVault: () -> Unit,
    viewModel: VaultViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val vaultUiState by viewModel.vaultUiState.collectAsState()
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            Scaffold(floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(AddVaultDestination.route)
                    },
                ) {
                    Icon(Icons.Filled.Add, "")
                }

            }, topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(R.string.app_vault_screen_title))
                }, actions = {
                    IconButton(onClick = { navigateToSearchVault() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "")
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
                    if (vaultUiState.vaultList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(vaultUiState.vaultList) { vault ->
                                val (expanded, onExpandedChange) = remember {
                                    mutableStateOf(false)
                                }
                                ListItem(headlineContent = { Text(vault.name) },
                                    supportingContent = { Text(vault.username) },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = "",
                                        )
                                    },
                                    trailingContent = {
                                        IconButton(onClick = { onExpandedChange(true) }) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = ""
                                            )
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        navigateToViewVault(vault.uid)
                                    })
                                HorizontalDivider()
                                VaultDialog(
                                    expanded,
                                    onExpandedChange,
                                    vault,
                                    clipboard,
                                    context,
                                    navigateToViewVault,
                                    navigateToEditVault
                                )
                            }
                        }
                    } else {
                        Text("No Vault")
                    }
                }
            }
        }
    }
}

fun checkVault(
    name: String, username: String, password: String,urlList:List<String>, context: Context
): Boolean {
    if (name.isEmpty() || name.isBlank()) {
        Toast.makeText(
            context, "請輸入名稱!", Toast.LENGTH_LONG
        ).show()
        return false
    } else if (username.isEmpty() || username.isBlank()) {
        Toast.makeText(
            context, "請輸入帳號!", Toast.LENGTH_LONG
        ).show()
        return false
    } else if (password.isEmpty() || password.isBlank()) {
        Toast.makeText(
            context, "請輸入密碼!", Toast.LENGTH_LONG
        ).show()
        return false
    }else if(urlList.contains("")){
        Toast.makeText(
            context, "請輸入網址!", Toast.LENGTH_LONG
        ).show()
        return false
    } else {
        return true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultDialog(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    vault: Vault,
    clipboardManager: ClipboardManager,
    context: Context,
    navigateToViewVault: (Int) -> Unit,
    navigateToEditVault: (Int) -> Unit
) {
    when {
        expanded -> {
            BasicAlertDialog(onDismissRequest = { onExpandedChange(false) }) {
                Column {
                    ListItem(headlineContent = { Text(vault.name) })
                    ListItem(headlineContent = { Text("View") }, leadingContent = {
                        Icon(
                            Icons.Outlined.Visibility, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        navigateToViewVault(vault.uid)
                        onExpandedChange(false)
                    })
                    ListItem(headlineContent = { Text("Edit") }, leadingContent = {
                        Icon(
                            Icons.Outlined.Edit, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        navigateToEditVault(vault.uid)
                        onExpandedChange(false)
                    })
                    ListItem(headlineContent = { Text("Copy Username") }, leadingContent = {
                        Icon(
                            Icons.Outlined.ContentCopy, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        copyText(
                            clipboardManager, vault.username, context
                        )
                        onExpandedChange(false)
                    })
                    ListItem(headlineContent = { Text("Copy Password") }, leadingContent = {
                        Icon(
                            Icons.Outlined.ContentCopy, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        copyPassword(
                            clipboardManager, vault.password, context
                        )
                        onExpandedChange(false)
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

fun copyText(clipboardManager: ClipboardManager, username: String, context: Context) {
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