package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.ui.theme.VaultGuardTheme


class ViewVaultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val intent = intent
                    val vault: Vault? = if (Build.VERSION.SDK_INT >= 33) {
                        intent.getParcelableExtra("vault", Vault::class.java)
                    } else {
                        intent.getParcelableExtra("vault")
                    }
                    val (isPasswordVisible, onPasswordVisibleChange) = remember {
                        mutableStateOf(false)
                    }
                    if (vault != null) {
                        ViewVault(vault, isPasswordVisible, onPasswordVisibleChange, this)
                    } else {
                        val activity = LocalContext.current as? Activity
                        activity?.finish()
                    }
                }
            }
        }
    }
}

@Composable
fun ViewVault(
    vault: Vault,
    isPasswordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    context: Context
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val activity = LocalContext.current as? Activity
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "查看密碼",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontSize = 32.sp
                )
                OutlinedTextField(
                    value = vault.name,
                    onValueChange = {
                    },
                    singleLine = true,
                    label = { Text("名稱") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(360.dp),
                    readOnly = true
                )
                OutlinedTextField(
                    value = vault.username,
                    onValueChange = {
                    },
                    singleLine = true,
                    label = { Text("帳號") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                    trailingIcon = {
                        IconButton(onClick = {
                            copyUsername(
                                clipboardManager,
                                vault.username,
                                context
                            )
                        }) {
                            Icon(Icons.Default.ContentCopy, "")
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(360.dp),
                    readOnly = true
                )
                OutlinedTextField(
                    value = vault.password,
                    onValueChange = {

                    },
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
                                    vault.password,
                                    context
                                )
                            }) {
                                Icon(Icons.Default.ContentCopy, "")
                            }
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(360.dp),
                    readOnly = true
                )

                Row {
                    Button(onClick = {
                        activity?.finish()
                    }, modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("返回")
                    }
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