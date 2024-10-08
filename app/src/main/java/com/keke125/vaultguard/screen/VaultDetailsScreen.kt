package com.keke125.vaultguard.screen

import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
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
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

object VaultDetailsDestination : NavigationDestination {
    override val route = "vault_details"
    override val titleRes = R.string.app_password_details_title
    const val VAULTID = "vaultId"
    val routeWithArgs = "$route/{$VAULTID}"
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
            val (totp, onTotpChange) = remember {
                mutableStateOf("")
            }
            val (time, onTimeChange) = remember {
                mutableIntStateOf(0)
            }
            val createdDateTime = if (Build.VERSION.SDK_INT >= 26) {
                if (uiState.value.vaultDetails.createdDateTime.isNotEmpty() and uiState.value.vaultDetails.createdDateTime.isNotBlank()) {
                    val parseFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    val showFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                    LocalDateTime.parse(uiState.value.vaultDetails.createdDateTime, parseFormatter)
                        .format(showFormatter)
                } else {
                    ""
                }
            } else {
                if (uiState.value.vaultDetails.createdDateTime.isNotEmpty() and uiState.value.vaultDetails.createdDateTime.isNotBlank()) {
                    val parseSimpleDateFormat =
                        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                    parseSimpleDateFormat.format(parseSimpleDateFormat.parse(uiState.value.vaultDetails.createdDateTime)!!)
                } else {
                    ""
                }
            }
            val lastModifiedDateTime = if (Build.VERSION.SDK_INT >= 26) {
                if (uiState.value.vaultDetails.lastModifiedDateTime.isNotEmpty() and uiState.value.vaultDetails.lastModifiedDateTime.isNotBlank()) {
                    val parseFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    val showFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                    LocalDateTime.parse(
                        uiState.value.vaultDetails.lastModifiedDateTime, parseFormatter
                    ).format(showFormatter)
                } else {
                    ""
                }
            } else {
                if (uiState.value.vaultDetails.lastModifiedDateTime.isNotEmpty() and uiState.value.vaultDetails.lastModifiedDateTime.isNotBlank()) {
                    val parseSimpleDateFormat =
                        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                    parseSimpleDateFormat.format(parseSimpleDateFormat.parse(uiState.value.vaultDetails.lastModifiedDateTime)!!)
                } else {
                    ""
                }
            }
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(VaultDetailsDestination.titleRes))
                }, actions = {
                    IconButton(onClick = { onMoreOptionsExpandedChange(true) }) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = "更多內容"
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一頁")
                    }
                })
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("${EditVaultDestination.route}/${uiState.value.vaultDetails.uid}")
                    },
                ) {
                    Icon(Icons.Filled.Edit, "編輯密碼")
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
                        text = stringResource(id = R.string.app_basic_info), modifier = Modifier.fillMaxWidth(0.8f), fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.name,
                        onValueChange = {},
                        singleLine = true,
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.app_password_name2)) },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                copyText(
                                    clipboardManager, uiState.value.vaultDetails.name, context
                                )
                            }) {
                                Icon(Icons.Default.ContentCopy, "複製名稱")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.username,
                        onValueChange = {},
                        singleLine = true,
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.app_password_username2)) },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                        trailingIcon = {
                            IconButton(onClick = {
                                copyText(
                                    clipboardManager, uiState.value.vaultDetails.username, context
                                )
                            }) {
                                Icon(Icons.Default.ContentCopy, "複製帳號")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.password,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.app_password_pw)) },
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
                                    copyPassword(
                                        clipboardManager,
                                        uiState.value.vaultDetails.password,
                                        context
                                    )
                                }) {
                                    Icon(Icons.Default.ContentCopy, "產生密碼")
                                }
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    if (uiState.value.vaultDetails.totp.isNotBlank() and uiState.value.vaultDetails.totp.isNotEmpty()) {
                        generateTotp(uiState.value.vaultDetails.totp, onTotpChange, onTimeChange)
                        OutlinedTextField(
                            value = totp,
                            onValueChange = {},
                            singleLine = true,
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.app_totp)) },
                            leadingIcon = { Icon(Icons.Default.Key, null) },
                            trailingIcon = {
                                Row {
                                    Text(
                                        text = time.toString(),
                                        modifier = Modifier.offset(x = 30.dp, y = 11.dp)
                                    )
                                    CircularProgressIndicator(
                                        progress = { ((time.toFloat() / 30.0)).toFloat() },
                                        modifier = Modifier.offset(y = 3.dp)
                                    )
                                    IconButton(onClick = {
                                        copyText(
                                            clipboardManager, totp, context
                                        )
                                    }) {
                                        Icon(Icons.Default.ContentCopy, "產生密碼")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                    OutlinedTextField(
                        value = if (viewModel.folderUiState.folder != null) viewModel.folderUiState.folder!!.name else stringResource(id = R.string.app_uncategorized),
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.app_folder)) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = uiState.value.vaultDetails.notes,
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.app_note)) },
                        minLines = 3,
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                copyText(
                                    clipboardManager, uiState.value.vaultDetails.notes, context
                                )
                            }) {
                                Icon(Icons.Default.ContentCopy, "複製備註")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = createdDateTime.toString(),
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.app_add_time)) },
                        minLines = 1,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = lastModifiedDateTime.toString(),
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.app_modify_time)) },
                        minLines = 1,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    if (uiState.value.vaultDetails.urlList.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                            text = stringResource(id = R.string.app_url),
                            modifier = Modifier.fillMaxWidth(0.8f),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.padding(vertical = 4.dp))
                        uiState.value.vaultDetails.urlList.forEachIndexed { _, url ->
                            ViewUrl(
                                url = url, context = context, clipboardManager = clipboardManager
                            )
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
                                Toast.makeText(context, context.getString(R.string.app_delete_password_success), Toast.LENGTH_SHORT).show()
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
        Icon(Icons.Default.Warning, "警告")
    }, title = {
        Text(text = stringResource(id = R.string.app_ask_delete_password))
    }, text = {
        Text(text = stringResource(id = R.string.app_ask_delete_password_desc))
    }, onDismissRequest = {
        onPasswordDeleteRequiredChange(false)
    }, confirmButton = {
        TextButton(onClick = {
            onDeleted()
            onPasswordDeleteRequiredChange(false)
        }) {
            Text( stringResource(id = R.string.app_confirm))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onPasswordDeleteRequiredChange(false)
        }) {
            Text( stringResource(id = R.string.app_cancel))
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
                    ListItem(headlineContent = { Text(stringResource(id = R.string.app_delete_password)) }, leadingContent = {
                        Icon(
                            Icons.Outlined.Delete, contentDescription = null
                        )
                    }, modifier = Modifier.clickable {
                        onExpandedChange(false)
                        onPasswordDeleteRequired(true)
                    })
                    ListItem(headlineContent = { }, trailingContent = {
                        TextButton(onClick = { onExpandedChange(false) }) {
                            Text(stringResource(id = R.string.app_cancel), color = ListItemDefaults.colors().headlineColor)
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun ViewUrl(url: String, context: Context, clipboardManager: ClipboardManager) {
    OutlinedTextField(
        value = if (url.startsWith("https://")) url.removePrefix("https://") else if (url.startsWith(
                "http://"
            )
        ) url.removePrefix("http://") else if (url.startsWith("androidapp://")) url.removePrefix("androidapp://") else url,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(stringResource(id = R.string.app_url)) },
        leadingIcon = { Icon(Icons.Default.Link, null) },
        trailingIcon = {
            Row {
                IconButton(onClick = {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (url.startsWith("androidapp://")) {
                            try {
                                val packageManager: PackageManager = context.packageManager
                                val intentSender: IntentSender =
                                    packageManager.getLaunchIntentSenderForPackage(
                                        url.removePrefix("androidapp://")
                                    )
                                intentSender.sendIntent(
                                    context, intentSender.creatorUid, null, null, null
                                )
                            } catch (e: IntentSender.SendIntentException) {
                                Toast.makeText(
                                    context, context.getString(R.string.app_open_url_error1), Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.app_open_url_error2),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (url.startsWith("https://") or url.startsWith("http://")) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                val uri = Uri.parse(url)
                                setData(uri)
                            }

                            try {
                                Toast.makeText(context, context.getString(R.string.app_open_url), Toast.LENGTH_SHORT).show()
                                startActivity(context, sendIntent, null)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.app_open_url_error2),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, context.getString(R.string.app_open_url_error3), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.app_open_url_error3), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (url.startsWith("androidapp://")) {
                            val packageManager: PackageManager = context.packageManager
                            val it =
                                packageManager.getLaunchIntentForPackage(url.removePrefix("androidapp://"))
                            if (it != null) {
                                startActivity(context, it, null)
                            } else {
                                Toast.makeText(
                                    context, context.getString(R.string.app_open_url_error1), Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (url.startsWith("https://") or url.startsWith("http://")) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                val uri = Uri.parse(url)
                                setData(uri)
                            }

                            try {
                                Toast.makeText(context, context.getString(R.string.app_open_url), Toast.LENGTH_SHORT).show()
                                startActivity(context, sendIntent, null)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.app_open_url_error2),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, context.getString(R.string.app_open_url_error3), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.app_open_url_error3), Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, "開啟網址")
                }
                IconButton(onClick = {
                    copyText(
                        clipboardManager, url, context
                    )
                }) {
                    Icon(Icons.Default.ContentCopy, "複製網址")
                }
            }
        },
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

fun generateTotp(
    base32EncodedSecret: String, updateTotp: (String) -> Unit, updateTime: (Int) -> Unit
) {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            val timestamp = Date(System.currentTimeMillis())
            val code =
                GoogleAuthenticator(base32EncodedSecret.encodeToByteArray()).generate(timestamp)
            val second: Int = if (Build.VERSION.SDK_INT >= 26) {
                30 - (LocalDateTime.now().second) % 30
            } else {
                30 - Calendar.getInstance().get(Calendar.SECOND) % 30
            }
            updateTime(second)
            updateTotp(code)
        }
    }, 0, 1000)
}