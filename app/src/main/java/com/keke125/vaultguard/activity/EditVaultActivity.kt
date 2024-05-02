package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.keke125.vaultguard.R
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.screen.checkPassword
import com.keke125.vaultguard.screen.generatePassword
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import com.keke125.vaultguard.util.DatabaseUtil


class EditVaultActivity : ComponentActivity() {
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
                    if (vault != null) {
                        EditVault(vault, this)
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
fun EditVault(
    vault: Vault,
    context: Context,
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (name, onNameChange) = remember {
                mutableStateOf(vault.name)
            }
            val (username, onUsernameChange) = remember {
                mutableStateOf(vault.username)
            }
            val (password, onPasswordChange) = remember {
                mutableStateOf(vault.password)
            }
            val (isPasswordGeneratorVisible, onPasswordGeneratorVisibleChange) = remember {
                mutableStateOf(false)
            }
            val activity = LocalContext.current as? Activity
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "編輯密碼",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontSize = 32.sp
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { onNameChange(it) },
                    singleLine = true,
                    label = { Text("名稱") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(360.dp),
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { onUsernameChange(it) },
                    singleLine = true,
                    label = { Text("帳號") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(360.dp),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        onPasswordChange(it)
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
                                onPasswordGeneratorVisibleChange(true)
                            }) {
                                Icon(Icons.Default.Refresh, "")
                            }
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(360.dp),
                )

                Row {
                    Button(onClick = {
                        if (checkPassword(name, username, password, context)) {
                            val newVault = Vault(vault.uid, name, username, password)
                            DatabaseUtil.updateVault(newVault, context)
                            onNameChange("")
                            onUsernameChange("")
                            onPasswordChange("")
                            activity?.finish()
                        }
                    }, modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                        Text("儲存")
                    }
                    Button(onClick = {
                        activity?.finish()
                    }, modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("取消")
                    }
                }

                when {
                    isPasswordGeneratorVisible -> {
                        PasswordGeneratorDialogConfirm(
                            onDismissRequest = { onPasswordGeneratorVisibleChange(false) },
                            context,
                            onPasswordChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordGeneratorDialogConfirm(
    onDismissRequest: () -> Unit, context: Context, onRealPasswordChange: (String) -> Unit
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
                        Icon(Icons.Default.Refresh, "")
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
                    Slider(value = length,
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
                        //onRealPasswordChange(password)
                        onConfirmChange(true)
                        //onDismissRequest()
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
            UpdatePasswordConfirm(
                onConfirmDismissRequest = { onConfirmChange(false) },
                onPasswordGeneratorDialogDismissRequest = { onDismissRequest() },
                password,
                onRealPasswordChange
            )
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
        Icon(Icons.Default.Warning, "")
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