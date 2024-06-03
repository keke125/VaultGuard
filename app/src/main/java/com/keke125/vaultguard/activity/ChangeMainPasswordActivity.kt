package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.ChangeMainPasswordUiState
import com.keke125.vaultguard.model.ChangeMainPasswordViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

class ChangeMainPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ChangeMainPasswordScreen(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeMainPasswordScreen(
    context: Context,
    viewModel: ChangeMainPasswordViewModel = viewModel(factory = AppViewModelProvider.Factory),
    changeMainPasswordUiState: ChangeMainPasswordUiState = viewModel.changeMainPasswordUiState
) {
    val activity = LocalContext.current as? Activity
    val (isOldPasswordVisible, onOldPasswordVisibleChange) = remember { mutableStateOf(false) }
    val (isNewPasswordVisible, onNewPasswordVisibleChange) = remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text("變更主密碼") }, navigationIcon = {
            IconButton(onClick = {
                activity?.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
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
            OutlinedTextField(
                value = changeMainPasswordUiState.changeMainPassword.oldPassword,
                onValueChange = {
                    viewModel.updateUiState(
                        changeMainPasswordUiState.changeMainPassword.copy(
                            oldPassword = it
                        )
                    )
                },
                label = { Text("舊密碼") },
                leadingIcon = { Icon(Icons.Default.Password, null) },
                trailingIcon = {
                    IconButton(onClick = { onOldPasswordVisibleChange(!isOldPasswordVisible) }) {
                        Icon(
                            imageVector = if (isOldPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            }, contentDescription = ""
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = if (isOldPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
            OutlinedTextField(
                value = changeMainPasswordUiState.changeMainPassword.newPassword,
                onValueChange = {
                    viewModel.updateUiState(
                        changeMainPasswordUiState.changeMainPassword.copy(
                            newPassword = it
                        )
                    )
                },
                label = { Text("新密碼") },
                leadingIcon = { Icon(Icons.Default.Password, null) },
                trailingIcon = {
                    IconButton(onClick = { onNewPasswordVisibleChange(!isNewPasswordVisible) }) {
                        Icon(
                            imageVector = if (isNewPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            }, contentDescription = ""
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = if (isNewPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
            Button(onClick = {
                if (changeMainPasswordUiState.changeMainPassword.oldPassword.isEmpty() || changeMainPasswordUiState.changeMainPassword.oldPassword.isBlank()) {
                    Toast.makeText(context, "請輸入舊密碼", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (changeMainPasswordUiState.changeMainPassword.newPassword.isEmpty() || changeMainPasswordUiState.changeMainPassword.newPassword.isBlank()) {
                    Toast.makeText(context, "請輸入新密碼", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val result = viewModel.changeMainPassword()
                if (result) {
                    viewModel.updateUiState(
                        changeMainPasswordUiState.changeMainPassword.copy(
                            oldPassword = "", newPassword = ""
                        )
                    )
                    Toast.makeText(context, "變更成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "變更失敗! 請確認舊密碼是否正確", Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
                Text(text = "變更")
            }
        }
    }
}