package com.keke125.vaultguard.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keke125.vaultguard.Screen
import com.keke125.vaultguard.activity.BiometricAuthActivity
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = { Text("設定") })
            }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ListItem(headlineContent = { Text("密碼庫") }, leadingContent = {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                        )
                    }, modifier = Modifier.clickable {
                        navController.navigate(Screen.VaultRepository.route)
                    })
                    HorizontalDivider()
                    ListItem(headlineContent = { Text("安全性") }, leadingContent = {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                        )
                    }, modifier = Modifier.clickable {
                        context.startActivity(Intent(context, BiometricAuthActivity::class.java))
                    })
                    HorizontalDivider()
                }
            }
        }
    }
}