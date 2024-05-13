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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val contentResolver = context.contentResolver
    val coroutineScope = rememberCoroutineScope()
    val openFileResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val url = result.data
                if (url != null) {
                    if (url.data != null) {
                        try {
                            contentResolver.openInputStream(url.data!!)?.use {
                                val vaults = viewModel.importVaultFromGooglePasswordManager(it)
                                if (vaults != null) {
                                    coroutineScope.launch {
                                        viewModel.saveVaults(vaults)
                                    }
                                    Toast.makeText(context, "匯入成功", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "匯入失敗", Toast.LENGTH_SHORT).show()
            }
        }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text("匯入密碼") }, navigationIcon = {
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
            Button(onClick = {
                val openFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/comma-separated-values"
                }
                openFileResultLauncher.launch(openFileIntent)
            }) {
                Text("匯入密碼")
            }
        }
    }
}
