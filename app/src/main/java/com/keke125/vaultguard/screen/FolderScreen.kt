package com.keke125.vaultguard.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.activity.LoginActivity
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.AuthViewModel
import com.keke125.vaultguard.model.FolderViewModel
import com.keke125.vaultguard.navigation.Screen
import com.keke125.vaultguard.ui.theme.VaultGuardTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    navController: NavController,
    navigateToViewFolder: (Int) -> Unit,
    folderViewModel: FolderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            val folderUiState by folderViewModel.foldersUiState.collectAsState()
            val (tryLoginAuth, onTryLoginAuthChange) = remember {
                mutableStateOf(false)
            }
            val (tryTimeout, onTryTimeoutChange) = remember {
                mutableStateOf(false)
            }
            if (authViewModel.isSignup()) {
                if ((authViewModel.isAuthenticated() && authViewModel.isNotTimeout()) || (tryLoginAuth && tryTimeout)) {
                    Scaffold(floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(AddFolderDestination.route)
                            },
                        ) {
                            Icon(Icons.Filled.Add, "新增資料夾")
                        }

                    }, topBar = {
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ), title = {
                            Text(stringResource(R.string.app_folder_screen_title))
                        })
                    }) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(innerPadding)
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ListItem(headlineContent = { Text("(未分類)") }, leadingContent = {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = "",
                                )
                            }, modifier = Modifier.clickable {
                                navigateToViewFolder(0)
                            })
                            HorizontalDivider()
                            if (folderUiState.folderList.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(folderUiState.folderList) { folder ->
                                        ListItem(headlineContent = {
                                            Text(
                                                folder.name,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }, leadingContent = {
                                            Icon(
                                                Icons.Default.Folder,
                                                contentDescription = "",
                                            )
                                        }, modifier = Modifier.clickable {
                                            navigateToViewFolder(folder.uid)
                                        })
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    startActivity(context, Intent(context, LoginActivity::class.java), null)
                    onTryLoginAuthChange(true)
                    onTryTimeoutChange(true)
                }
            } else {
                navController.navigate(Screen.Signup.route)
            }
        }
    }
}

fun checkFolder(
    name: String, context: Context
): Boolean {
    if (name.isEmpty() || name.isBlank()) {
        Toast.makeText(
            context, "請輸入名稱!", Toast.LENGTH_LONG
        ).show()
        return false
    } else {
        return true
    }
}