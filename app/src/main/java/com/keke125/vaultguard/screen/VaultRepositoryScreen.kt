package com.keke125.vaultguard.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keke125.vaultguard.activity.DeleteVaultsActivity
import com.keke125.vaultguard.activity.ExportVaultActivity
import com.keke125.vaultguard.activity.ImportVaultActivity
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import com.keke125.vaultguard.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultsRepositoryScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = { Text(stringResource(id = R.string.app_vault_setting_title)) }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一頁")
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
                    ListItem(headlineContent = { Text(stringResource(id = R.string.app_import_vault)) },
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, ImportVaultActivity::class.java))
                        })
                    HorizontalDivider()
                    ListItem(headlineContent = { Text(stringResource(id = R.string.app_export_vault)) }, modifier = Modifier.clickable {
                        context.startActivity(Intent(context, ExportVaultActivity::class.java))
                    })
                    HorizontalDivider()
                    ListItem(headlineContent = { Text(stringResource(id = R.string.app_clear_vault)) }, modifier = Modifier.clickable {
                        context.startActivity(Intent(context, DeleteVaultsActivity::class.java))
                    })
                    HorizontalDivider()
                }
            }
        }
    }
}