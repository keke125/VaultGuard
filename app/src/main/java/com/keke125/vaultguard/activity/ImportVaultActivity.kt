package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keke125.vaultguard.R
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.ImportVaultViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException

class ImportVaultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ImportVaultScreen(context = this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportVaultScreen(
    viewModel: ImportVaultViewModel = viewModel(factory = AppViewModelProvider.Factory),
    context: Context
) {
    val activity = LocalContext.current as? Activity
    val vaultUiState by viewModel.vaultUiState.collectAsState()
    val folderUiState by viewModel.folderUiState.collectAsState()
    val contentResolver = context.contentResolver
    val coroutineScope = rememberCoroutineScope()
    val (isDropdownExpanded, onDropdownExpandedChange) = remember { mutableStateOf(false) }
    val (importType, onImportTypeChange) = remember { mutableIntStateOf(0) }
    val (importTypeName, onImportTypeNameChange) = remember { mutableStateOf(context.getString(R.string.app_google_pw_manager)) }
    val openGPMResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val url = result.data
                if (url != null) {
                    if (url.data != null) {
                        try {
                            contentResolver.openInputStream(url.data!!)?.use {
                                val vaults = viewModel.importVaultFromGPM(it)
                                if (vaults != null) {
                                    coroutineScope.launch {
                                        viewModel.saveVaults(vaults)
                                    }
                                    Toast.makeText(context, context.getString(R.string.app_import_success), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
            }
        }
    val openVGResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val url = result.data
                if (url != null) {
                    if (url.data != null) {
                        try {
                            contentResolver.openInputStream(url.data!!)?.use {
                                val vaultsAndFolders = viewModel.importVaultAndFolderFromVG(it)
                                if (vaultsAndFolders != null) {
                                    val vaults = vaultsAndFolders.first
                                    val folders = vaultsAndFolders.second
                                    coroutineScope.launch {
                                        viewModel.saveFolders(folders)
                                        viewModel.saveVaults(vaults)
                                    }
                                    Toast.makeText(context, context.getString(R.string.app_import_success), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, context.getString(R.string.app_import_fail), Toast.LENGTH_SHORT).show()
            }
        }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text(stringResource(id = R.string.app_import_vault)) }, navigationIcon = {
            IconButton(onClick = {
                activity?.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
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
            Column {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { onDropdownExpandedChange(!isDropdownExpanded) },
                ) {
                    OutlinedTextField(
                        value = importTypeName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.app_import_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(0.8f)
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { onDropdownExpandedChange(false) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(id = R.string.app_google_pw_manager)) }, onClick = {
                            onImportTypeNameChange(context.getString(R.string.app_google_pw_manager))
                            onImportTypeChange(0)
                            onDropdownExpandedChange(false)
                        })
                        DropdownMenuItem(text = { Text(stringResource(id = R.string.app_name)) }, onClick = {
                            onImportTypeNameChange(context.getString(R.string.app_name))
                            onImportTypeChange(1)
                            onDropdownExpandedChange(false)
                        })
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Button(onClick = {
                    if (importType == 0) {
                        val openGPMIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            val mimeTypes = arrayOf("text/csv", "text/comma-separated-values")
                            type = "*/*"
                            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                        }
                        openGPMResultLauncher.launch(openGPMIntent)
                    } else {
                        if (vaultUiState.vaultList.isEmpty() && folderUiState.folderList.isEmpty()) {
                            val openVGIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json"
                            }
                            openVGResultLauncher.launch(openVGIntent)
                        } else {
                            Toast.makeText(context, context.getString(R.string.app_clear_vault_required), Toast.LENGTH_SHORT).show()
                        }
                    }
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(id = R.string.app_import_vault))
                }
            }
        }
    }
}
