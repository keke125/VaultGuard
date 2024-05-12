package com.keke125.vaultguard.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keke125.vaultguard.model.AppViewModelProvider
import com.keke125.vaultguard.model.ExportVaultViewModel
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ExportVaultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VaultGuardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ExportVaultScreen(context = this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportVaultScreen(
    viewModel: ExportVaultViewModel = viewModel(factory = AppViewModelProvider.Factory),
    context: Context
) {
    val vaultUiState by viewModel.vaultUiState.collectAsState()
    val activity = LocalContext.current as? Activity
    val jsonString = viewModel.exportVault(vaultUiState.vaultList)
    val contentResolver = context.contentResolver
    val createFileResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val url = result.data
                if (url != null && jsonString != null) {
                    if (url.data != null) {
                        try {
                            contentResolver.openFileDescriptor(url.data!!, "w")?.use { it ->
                                FileOutputStream(it.fileDescriptor).use {
                                    it.write(
                                        (jsonString).toByteArray()
                                    )
                                }
                            }
                            Toast.makeText(context, "匯出成功", Toast.LENGTH_SHORT).show()
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(context, "匯出失敗", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, "匯出失敗", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "匯出失敗", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "匯出失敗", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "匯出失敗", Toast.LENGTH_SHORT).show()
            }
        }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text("匯出密碼") }, navigationIcon = {
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
                //val getUriIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                //getUriResultLauncher.launch(getUriIntent)
                val timeStamp: String
                if (Build.VERSION.SDK_INT >= 26) {
                    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    timeStamp = LocalDateTime.now().format(formatter).toString()
                } else {
                    val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    timeStamp = simpleDateFormat.format(Date())
                }
                val createFileIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "vaultguard-$timeStamp-export.json")
                }
                createFileResultLauncher.launch(createFileIntent)
            }) {
                Text("匯出密碼")
            }
        }
    }
}
