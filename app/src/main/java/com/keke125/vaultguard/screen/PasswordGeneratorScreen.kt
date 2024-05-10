package com.keke125.vaultguard.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import java.security.SecureRandom

@Composable
fun PasswordGeneratorScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val (length, onLengthChange) = remember {
                mutableFloatStateOf(3f)
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
                        length.toInt(),
                        isUpper,
                        isLower,
                        isNumber,
                        isSpecialChar,
                        navController.context
                    )
                )
            }
            val clipboard =
                navController.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            PasswordGenerator(
                navController,
                length,
                onLengthChange,
                isUpper,
                onUpperChange,
                isLower,
                onLowerChange,
                isNumber,
                onNumberChange,
                isSpecialChar,
                onSpecialCharChange,
                clipboard,
                password,
                onPasswordChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGenerator(
    navController: NavController,
    length: Float,
    onLengthChange: (Float) -> Unit,
    isUpper: Boolean,
    onUpperChange: (Boolean) -> Unit,
    isLower: Boolean,
    onLowerChange: (Boolean) -> Unit,
    isNumber: Boolean,
    onNumberChange: (Boolean) -> Unit,
    isSpecialChar: Boolean,
    onSpecialCharChange: (Boolean) -> Unit,
    clipboard: ClipboardManager,
    password: String,
    onPasswordChange: (String) -> Unit
) {
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
            Text(stringResource(R.string.app_password_generator_screen_title))
        })
    }) { innerPadding ->
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(innerPadding)
        .padding(vertical = 8.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp)
                .padding(vertical = 16.dp)
        )
        SelectionContainer {
            Text(
                text = password,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .padding(end = 16.dp),
                fontSize = 24.sp,
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ContextCompat.getString(navController.context, R.string.app_length),
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = length.toInt().toString(), fontSize = 20.sp)
            Spacer(modifier = Modifier.padding(8.dp))
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
                            navController.context
                        )
                    )
                },
                valueRange = 3f..128f,
                modifier = Modifier.padding(end = 16.dp),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .toggleable(
                    value = isUpper, onValueChange = {
                        onUpperChange(!isUpper)
                        onPasswordChange(
                            generatePassword(
                                length.toInt(),
                                it,
                                isLower,
                                isNumber,
                                isSpecialChar,
                                navController.context
                            )
                        )
                    }, role = Role.Checkbox
                )
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isUpper, onCheckedChange = {
                onPasswordChange(
                    generatePassword(
                        length.toInt(), it, isLower, isNumber, isSpecialChar, navController.context
                    )
                )
                onUpperChange(!isUpper)
            })
            Text(
                text = ContextCompat.getString(navController.context, R.string.app_upper),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .toggleable(
                    value = isLower, onValueChange = {
                        onLowerChange(!isLower)
                        onPasswordChange(
                            generatePassword(
                                length.toInt(),
                                isUpper,
                                it,
                                isNumber,
                                isSpecialChar,
                                navController.context
                            )
                        )
                    }, role = Role.Checkbox
                )
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isLower, onCheckedChange = {
                onPasswordChange(
                    generatePassword(
                        length.toInt(), isUpper, it, isNumber, isSpecialChar, navController.context
                    )
                )
                onLowerChange(!isLower)
            })
            Text(
                text = ContextCompat.getString(navController.context, R.string.app_lower),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .toggleable(
                    value = isNumber, onValueChange = {
                        onNumberChange(!isNumber)
                        onPasswordChange(
                            generatePassword(
                                length.toInt(),
                                isUpper,
                                isLower,
                                it,
                                isSpecialChar,
                                navController.context
                            )
                        )
                    }, role = Role.Checkbox
                )
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isNumber, onCheckedChange = {
                onPasswordChange(
                    generatePassword(
                        length.toInt(), isUpper, isLower, it, isSpecialChar, navController.context
                    )
                )
                onNumberChange(!isNumber)
            })
            Text(
                text = ContextCompat.getString(navController.context, R.string.app_number),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .toggleable(
                    value = isSpecialChar, onValueChange = {
                        onSpecialCharChange(!isSpecialChar)
                        onPasswordChange(
                            generatePassword(
                                length.toInt(),
                                isUpper,
                                isLower,
                                isNumber,
                                it,
                                navController.context
                            )
                        )
                    }, role = Role.Checkbox
                )
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isSpecialChar, onCheckedChange = {
                onPasswordChange(
                    generatePassword(
                        length.toInt(), isUpper, isLower, isNumber, it, navController.context
                    )
                )
                onSpecialCharChange(!isSpecialChar)
            })
            Text(
                text = ContextCompat.getString(navController.context, R.string.app_special_char),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    onPasswordChange(
                        generatePassword(
                            length.toInt(),
                            isUpper,
                            isLower,
                            isNumber,
                            isSpecialChar,
                            navController.context
                        )
                    )
                }, modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    ContextCompat.getString(
                        navController.context, R.string.app_regenerate_password
                    ), fontSize = 16.sp
                )
            }
            Button(
                onClick = { copyPassword(password, clipboard, navController.context) },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    ContextCompat.getString(navController.context, R.string.app_copy_password),
                    fontSize = 16.sp
                )
            }
        }
    }
    }
}

fun generatePassword(
    length: Int,
    isUpper: Boolean,
    isLower: Boolean,
    isNumber: Boolean,
    isSpecialChar: Boolean,
    context: Context
): String {
    val charSet = mutableListOf<Char>()
    val secureRandom = SecureRandom()
    if (!isUpper && !isLower && !isNumber && !isSpecialChar) {
        Toast.makeText(
            context,
            ContextCompat.getString(context, R.string.app_charset_required),
            Toast.LENGTH_LONG
        ).show()
        return ""
    } else {
        if (isUpper) {
            charSet.addAll('A'..'Z')
        }
        if (isLower) {
            charSet.addAll('a'..'z')
        }
        if (isNumber) {
            charSet.addAll('0'..'9')
        }
        if (isSpecialChar) {
            charSet.add('!')
            charSet.add('@')
            charSet.add('#')
            charSet.add('$')
            charSet.add('%')
            charSet.add('^')
            charSet.add('*')
        }
        return (1..length).joinToString(separator = "") { charSet[secureRandom.nextInt(charSet.size)].toString() }
    }
}

fun copyPassword(password: String, clipboard: ClipboardManager, context: Context) {
    // When setting the clipboard text.
    clipboard.setPrimaryClip(ClipData.newPlainText("password", password))
    Toast.makeText(
        context, ContextCompat.getString(context, R.string.app_copied_password), Toast.LENGTH_SHORT
    ).show()
}