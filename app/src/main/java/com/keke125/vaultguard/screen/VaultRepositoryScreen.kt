package com.keke125.vaultguard.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.activity.ExportVaultActivity
import com.keke125.vaultguard.activity.ImportVaultActivity
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.DeleteVaultsViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultsRepositoryScreen(navController: NavController, viewModel: DeleteVaultsViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val coroutineScope = rememberCoroutineScope()
            val deleteVaultsUiState by viewModel.vaultUiState.collectAsState()
            val (isDeleteConfirmExpanded, onDeleteConfirmExpandedChange) = remember { mutableStateOf(false) }
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = { Text("密碼庫設定") }, navigationIcon = {
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
                    ListItem(headlineContent = { Text("匯入密碼") },
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, ImportVaultActivity::class.java))
                        })
                    HorizontalDivider()
                    ListItem(headlineContent = { Text("匯出密碼") }, modifier = Modifier.clickable {
                        context.startActivity(Intent(context, ExportVaultActivity::class.java))
                    })
                    HorizontalDivider()
                    ListItem(headlineContent = { Text("清空密碼庫") }, modifier = Modifier.clickable {
                        onDeleteConfirmExpandedChange(true)
                    })
                    HorizontalDivider()
                    when {
                        isDeleteConfirmExpanded -> {
                            DeleteVaultsConfirm(onDeleteConfirmExpandedChange, onDeleted = {
                                if(deleteVaultsUiState.vaultList.isNotEmpty()){
                                    coroutineScope.launch {
                                        viewModel.deleteVault(deleteVaultsUiState.vaultList)
                                    }
                                    navController.popBackStack()
                                    Toast.makeText(context, "已清空密碼庫", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "密碼庫尚未儲存密碼!", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteVaultsConfirm(
    onPasswordDeleteRequiredChange: (Boolean) -> Unit,
    onDeleted: () -> Unit,
) {
    AlertDialog(icon = {
        Icon(Icons.Default.Warning, "警告")
    }, title = {
        Text(text = "是否要清空密碼庫?")
    }, text = {
        Text(text = "所有密碼將被刪除!")
    }, onDismissRequest = {
        onPasswordDeleteRequiredChange(false)
    }, confirmButton = {
        TextButton(onClick = {
            onPasswordDeleteRequiredChange(false)
        }) {
            Text("取消")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDeleted()
            onPasswordDeleteRequiredChange(false)
        }) {
            Text("確定")
        }
    })
}