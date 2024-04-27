package com.keke125.vaultguard.screen

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import com.keke125.vaultguard.util.DatabaseUtil

@Composable
fun VaultScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column {
                Text(
                    "Vault Screen",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
fun AddVaultScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val (name, onNameChange) = remember {
                mutableStateOf("")
            }
            val (username, onUsernameChange) = remember {
                mutableStateOf("")
            }
            val (password, onPasswordChange) = remember {
                mutableStateOf("")
            }
            val (isPasswordVisible, onPasswordVisibleChange) = remember {
                mutableStateOf(false)
            }
            val (isPasswordGeneratorVisible, onPasswordGeneratorVisibleChange) = remember {
                mutableStateOf(false)
            }
            val context = navController.context
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "新增密碼",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontSize = 32.sp
                )
                OutlinedTextField(value = name,
                    onValueChange = {
                        onNameChange(it)
                    },
                    singleLine = true,
                    label = { Text("名稱") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(320.dp)
                )
                OutlinedTextField(value = username,
                    onValueChange = {
                        onUsernameChange(it)
                    },
                    singleLine = true,
                    label = { Text("帳號") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(320.dp)
                )
                OutlinedTextField(value = password,
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
                        .width(320.dp)
                )
                Button(onClick = {
                    if (checkPassword(name, username, password, context)) {
                        DatabaseUtil.savePassword(name, username, password, context)
                        onNameChange("")
                        onUsernameChange("")
                        onPasswordChange("")
                    }
                }, modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("儲存")
                }
            }
            when {
                isPasswordGeneratorVisible -> {
                    PasswordGeneratorDialog(
                        onDismissRequest = { onPasswordGeneratorVisibleChange(false) },
                        navController,
                        onPasswordChange
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordGeneratorDialog(
    onDismissRequest: () -> Unit,
    navController: NavController,
    onRealPasswordChange: (String) -> Unit
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
                length.toInt(), isUpper, isLower, isNumber, isSpecialChar, navController
            )
        )
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
                    modifier = Modifier.padding(start = 12.dp)
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
                                length.toInt(),
                                isUpper,
                                isLower,
                                isNumber,
                                isSpecialChar,
                                navController
                            )
                        )
                    }) {
                        Icon(Icons.Default.Refresh, "")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = ContextCompat.getString(navController.context, R.string.app_length),
                        fontSize = 20.sp
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
                                    navController
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
                                    length.toInt(),
                                    it,
                                    isLower,
                                    isNumber,
                                    isSpecialChar,
                                    navController
                                )
                            )
                        }, role = Role.Checkbox
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isUpper, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), it, isLower, isNumber, isSpecialChar, navController
                            )
                        )
                        onUpperChange(!isUpper)
                    })
                    Text(
                        text = ContextCompat.getString(navController.context, R.string.app_upper),
                        fontSize = 18.sp
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isLower,
                        onValueChange = {
                            onLowerChange(!isLower)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(),
                                    isUpper,
                                    it,
                                    isNumber,
                                    isSpecialChar,
                                    navController
                                )
                            )
                        },
                        role = Role.Checkbox,
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isLower, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, it, isNumber, isSpecialChar, navController
                            )
                        )
                        onLowerChange(!isLower)
                    })
                    Text(
                        text = ContextCompat.getString(navController.context, R.string.app_lower),
                        fontSize = 20.sp
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isNumber, onValueChange = {
                            onNumberChange(!isNumber)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(),
                                    isUpper,
                                    isLower,
                                    it,
                                    isSpecialChar,
                                    navController
                                )
                            )
                        }, role = Role.Checkbox
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isNumber, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, isLower, it, isSpecialChar, navController
                            )
                        )
                        onNumberChange(!isNumber)
                    })
                    Text(
                        text = ContextCompat.getString(navController.context, R.string.app_number),
                        fontSize = 20.sp
                    )
                }
                Row(
                    Modifier.toggleable(
                        value = isSpecialChar, onValueChange = {
                            onSpecialCharChange(!isSpecialChar)
                            onPasswordChange(
                                generatePassword(
                                    length.toInt(), isUpper, isLower, isNumber, it, navController
                                )
                            )
                        }, role = Role.Checkbox
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSpecialChar, onCheckedChange = {
                        onPasswordChange(
                            generatePassword(
                                length.toInt(), isUpper, isLower, isNumber, it, navController
                            )
                        )
                        onSpecialCharChange(!isSpecialChar)
                    })
                    Text(
                        text = ContextCompat.getString(
                            navController.context, R.string.app_special_char
                        ), fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.padding(start = 136.dp)) {
                    TextButton(onClick = {
                        onRealPasswordChange(password)
                        onDismissRequest()
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