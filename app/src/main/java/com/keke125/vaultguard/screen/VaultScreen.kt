package com.keke125.vaultguard.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keke125.vaultguard.data.AppDB
import com.keke125.vaultguard.data.OfflineVaultsRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import kotlinx.coroutines.launch

@Composable
fun VaultScreen(navController: NavController) {
    VaultGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val db = AppDB.getDatabase(navController.context)
            val vaultDAO = db.vaultDAO()
            val testVault =
                Vault(name = "testName", username = "testUsername", password = "testPassword")
            val offlineVaultsRepository = OfflineVaultsRepository(vaultDAO)
            val coroutineScope = rememberCoroutineScope()
            val testdata = offlineVaultsRepository.getAllVaults()
            Column {
                Text(
                    "Vault Screen",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
                if (testdata.isEmpty()) {
                    Text(text = "Success")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        offlineVaultsRepository.insertVault(
                            testVault
                        )
                    }
                }) {
                    Text("add test data")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        val testVaults = offlineVaultsRepository.getAllVaults()
                        for (vault in testVaults){
                            offlineVaultsRepository.deleteVault(vault)
                        }
                    }
                }) {
                    Text("remove")
                }
                LazyColumn {
                }
            }
        }
    }
}