package com.keke125.vaultguard.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keke125.vaultguard.data.AppDB
import com.keke125.vaultguard.data.OfflineVaultsRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

@Composable
fun VaultScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val db = AppDB.getDatabase(navController.context)
            val vaultDAO = db.vaultDAO()
            val testVault =
                Vault(name = "testName", username = "testUsername", password = "testPassword")
            val offlineVaultsRepository = OfflineVaultsRepository(vaultDAO)
            val coroutineScope = rememberCoroutineScope()
            val testdata = offlineVaultsRepository.getAllVaults()
            Column {
                Text(
                    "Vault Screen",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
                if (testdata.isEmpty()) {
                    Text(text = "Success")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        offlineVaultsRepository.insertVault(
                            testVault
                        )
                    }
                }) {
                    Text("add test data")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        val testVaults = offlineVaultsRepository.getAllVaults()
                        for (vault in testVaults){
                            offlineVaultsRepository.deleteVault(vault)
                        }
                    }
                }) {
                    Text("remove")
                }
                LazyColumn {
                }
            }
        }
    }
}

@Composable
fun AddVaultScreen(navController: NavController){
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
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
            Column {
                Text(
                    text = "新增密碼",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp)
                        .padding(vertical = 16.dp),
                    fontSize = 32.sp
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        onNameChange(it)
                    },
                    singleLine = true,
                    label = { Text("名稱") },
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .padding(vertical = 16.dp)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        onUsernameChange(it)
                    },
                    singleLine = true,
                    label = { Text("帳號") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle,"")},
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .padding(vertical = 16.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        onPasswordChange(it)
                    },
                    label = { Text("密碼") },
                    leadingIcon = { Icon(Icons.Default.Password,"")},
                    trailingIcon = { Row {
                        IconButton(onClick = { onPasswordVisibleChange(!isPasswordVisible) }) {
                            if(isPasswordVisible){
                                Icon(Icons.Default.VisibilityOff,"")
                            }else{
                                Icon(Icons.Default.Visibility,"")
                            }
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.Refresh,"")
                        }
                    } },
                    visualTransformation= if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}