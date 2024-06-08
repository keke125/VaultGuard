package com.keke125.vaultguard.screen

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.FolderDetailsViewModel
import com.keke125.vaultguard.navigation.NavigationDestination
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

object FolderDetailsDestination : NavigationDestination {
    override val route = "folder_details"
    override val titleRes = R.string.app_folder_details_title
    const val FOLDERID = "folderId"
    val routeWithArgs = "$route/{$FOLDERID}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailsScreen(
    navController: NavController,
    navigateToViewVault: (Int) -> Unit,
    navigateToEditVault: (Int) -> Unit,
    viewModel: FolderDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background

        ) {
            val context = navController.context
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val uiState = viewModel.uiState.collectAsState()
            val vaultUiState = viewModel.vaultUiState.collectAsState()
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(
                        text = if (viewModel.folderId == 0) String.format(
                            stringResource(
                                FolderDetailsDestination.titleRes
                            ), "(未分類)"
                        ) else String.format(
                            stringResource(FolderDetailsDestination.titleRes),
                            uiState.value.folderDetails.name
                        ), maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
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
                        navController.navigate("${AddVaultDestination.route}/${viewModel.folderId}")
                    },
                ) {
                    Icon(Icons.Filled.Add, "新增密碼")
                }
            }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (vaultUiState.value.vaultList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(vaultUiState.value.vaultList) { vault ->
                                val (vaultExpanded, onVaultExpandedChange) = remember {
                                    mutableStateOf(false)
                                }
                                ListItem(headlineContent = {
                                    Text(
                                        vault.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }, supportingContent = {
                                    Text(
                                        vault.username,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }, leadingContent = {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "",
                                    )
                                }, trailingContent = {
                                    IconButton(onClick = { onVaultExpandedChange(true) }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "更多內容"
                                        )
                                    }
                                }, modifier = Modifier.clickable {
                                    navigateToViewVault(vault.uid)
                                })
                                HorizontalDivider()
                                VaultDialog(
                                    vaultExpanded,
                                    onVaultExpandedChange,
                                    vault,
                                    clipboard,
                                    context,
                                    navigateToViewVault,
                                    navigateToEditVault
                                )
                            }
                        }
                    } else {
                        Text("尚未儲存密碼")
                    }
                }
            }
        }
    }
}