package com.keke125.vaultguard.screen

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import com.keke125.vaultguard.util.DatabaseUtil

@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@Composable
fun VaultScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val vaults =  DatabaseUtil.getAllVaults().asLiveData()
            val vaultsState = vaults.observeAsState(emptyList())
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
                            ListItem(
                                headlineContent = { Text(vault.name) },
                                supportingContent = { Text(vault.username) },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "",
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                                    }
                                },
                                modifier = Modifier.clickable{

                                }
                            )
                            HorizontalDivider()
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