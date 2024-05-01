package com.keke125.vaultguard.screen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.keke125.vaultguard.activity.ViewVaultActivity
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import com.keke125.vaultguard.util.DatabaseUtil

@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@Composable
fun VaultScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val vaults = DatabaseUtil.getAllVaults().asLiveData()
            val vaultsState = vaults.observeAsState(emptyList())
            val context = navController.context
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            Column {
                Text(
                    "Vault Screen",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
                if (vaultsState.value.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(vaultsState.value) { vault ->
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
                                    val intent = Intent()
                                    intent.setClass(context, ViewVaultActivity::class.java)
                                    intent.putExtra("vault", vault)
                                    context.startActivity(intent)
                                })
                            HorizontalDivider()
                            VaultDialog(expanded, onExpandedChange, vault, clipboard, context)
                        }
                    }
                }
            }
        }
    }
}

fun checkPassword(
    name: String, username: String, password: String, context: Context
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
    context: Context
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
                        val intent = Intent()
                        intent.setClass(context, ViewVaultActivity::class.java)
                        intent.putExtra("vault", vault)
                        context.startActivity(intent)
                    })
                    ListItem(headlineContent = { Text("Edit") }, leadingContent = {
                        Icon(
                            Icons.Outlined.Edit, contentDescription = ""
                        )
                    })
                    ListItem(headlineContent = { Text("Copy Username") }, leadingContent = {
                        Icon(
                            Icons.Outlined.ContentCopy, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        copyUsername(
                            clipboardManager, vault.username, context
                        )
                    })
                    ListItem(headlineContent = { Text("Copy Password") }, leadingContent = {
                        Icon(
                            Icons.Outlined.ContentCopy, contentDescription = ""
                        )
                    }, modifier = Modifier.clickable {
                        copyPassword(
                            clipboardManager, vault.password, context
                        )
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