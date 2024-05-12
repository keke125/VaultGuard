package com.keke125.vaultguard.screen

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.SearchVaultViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchVaultScreen(
    navController: NavController,
    navigateToViewVault: (Int) -> Unit,
    navigateToEditVault: (Int) -> Unit,
    viewModel: SearchVaultViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val vaultUiState by viewModel.searchVaultUiState.collectAsState()
            val context = navController.context
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val (keyword, onKeywordChange) = remember { mutableStateOf("") }
            val (isSearchActive, onSearchActiveChange) = remember { mutableStateOf(false) }
            val focusRequester = remember { FocusRequester() }
            var textFieldLoaded by remember { mutableStateOf(false) }
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                }, title = { Text("搜尋密碼") })
            }) { innerPadding ->
                SearchBar(query = keyword, onQueryChange = {
                    onKeywordChange(it)
                    viewModel.updateKeyword(it)
                }, onSearch = {
                    if (it == "") {
                        onSearchActiveChange(false)
                    } else {
                        onKeywordChange(it)
                        viewModel.updateKeyword(it)
                    }
                }, leadingIcon = {
                    Icon(
                        Icons.Default.Search, contentDescription = ""
                    )
                }, trailingIcon = {
                    IconButton(onClick = {
                        onKeywordChange("")
                        viewModel.updateKeyword("")
                    }) {
                        Icon(Icons.Rounded.Cancel, contentDescription = "")
                    }
                }, active = isSearchActive, onActiveChange = {
                    onSearchActiveChange(it)
                }, placeholder = { Text("搜尋") }, content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (vaultUiState.vaultList.isNotEmpty() && keyword.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(vaultUiState.vaultList) { vault ->
                                    val (expanded, onExpandedChange) = remember {
                                        mutableStateOf(false)
                                    }
                                    ListItem(headlineContent = { Text(vault.name) },
                                        supportingContent = { Text(vault.username) },
                                        leadingContent = {
                                            Icon(
                                                Icons.Default.AccountCircle,
                                                contentDescription = "",
                                            )
                                        },
                                        trailingContent = {
                                            IconButton(onClick = { onExpandedChange(true) }) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = ""
                                                )
                                            }
                                        },
                                        modifier = Modifier.clickable {
                                            navigateToViewVault(vault.uid)
                                        })
                                    HorizontalDivider()
                                    VaultDialog(
                                        expanded,
                                        onExpandedChange,
                                        vault,
                                        clipboard,
                                        context,
                                        navigateToViewVault,
                                        navigateToEditVault
                                    )
                                }
                            }
                        } else if (keyword.isNotEmpty()) {
                            Text("找不到相符的密碼")
                        }
                    }
                }, modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        }
                        .padding(innerPadding)
                )
            }
        }
    }
}