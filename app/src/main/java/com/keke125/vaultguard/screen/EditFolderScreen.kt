package com.keke125.vaultguard.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.EditFolderViewModel
import com.keke125.vaultguard.navigation.NavigationDestination
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

object EditFolderDestination : NavigationDestination {
    override val route = "edit_folder"
    override val titleRes = R.string.app_edit_folder_title
    const val FOLDERID = "folderId"
    val routeWithArgs = "$route/{$FOLDERID}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFolderScreen(
    viewModel: EditFolderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val context = navController.context
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(EditFolderDestination.titleRes))
                }, actions = {
                    TextButton(onClick = {
                        if (checkFolder(
                                viewModel.folderUiState.folderDetails.name, context
                            )
                        ) {
                            coroutineScope.launch {
                                viewModel.updateFolder()
                            }
                            navController.popBackStack()
                            Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("更新")
                    }
                }, navigationIcon = {
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
                        .padding(vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "基本資訊", modifier = Modifier.fillMaxWidth(0.8f), fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    OutlinedTextField(
                        value = viewModel.folderUiState.folderDetails.name,
                        onValueChange = {
                            viewModel.updateUiState(viewModel.folderUiState.folderDetails.copy(name = it))
                        },
                        singleLine = true,
                        label = { Text("名稱(必填)") },
                        leadingIcon = { Icon(Icons.Default.Folder, null) },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        }
    }
}