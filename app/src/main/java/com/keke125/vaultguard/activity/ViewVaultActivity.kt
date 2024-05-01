package com.keke125.vaultguard.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
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
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
                    val vault: Vault? = if (Build.VERSION.SDK_INT >= 33){
                        intent.getParcelableExtra("vault", Vault::class.java)
                    }else{
                        intent.getParcelableExtra("vault")
                    }
                    val (isPasswordVisible, onPasswordVisibleChange) = remember {
                        mutableStateOf(false)
                    }
                    if (vault != null) {
                        ViewVault(vault,isPasswordVisible, onPasswordVisibleChange)
                    }else{
                        val activity = LocalContext.current as? Activity
                        activity?.finish()
                    }
                }
            }
        }
    }
}

@Composable
fun ViewVault(vault: Vault, isPasswordVisible: Boolean, onPasswordVisibleChange: (Boolean) -> Unit) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val activity = LocalContext.current as? Activity
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "查看密碼",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontSize = 32.sp
                )
                TextField(
                    value = vault.name,
                    onValueChange = {
                    },
                    singleLine = true,
                    label = { Text("名稱") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(320.dp),
                    readOnly = true
                )
                TextField(
                    value = vault.username,
                    onValueChange = {
                    },
                    singleLine = true,
                    label = { Text("帳號") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, "") },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(320.dp),
                    readOnly = true
                )
                TextField(
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
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(320.dp),
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