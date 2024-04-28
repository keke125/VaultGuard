package com.keke125.vaultguard.util

import android.content.Context
import android.widget.Toast
import com.keke125.vaultguard.MainActivity
import com.keke125.vaultguard.data.Vault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DatabaseUtil {
    companion object{
        private val offlineVaultsRepository = MainActivity.getOfflineVaultsRepository()
        fun saveVault(name: String, username: String, password: String, context: Context){
            val vault = Vault(name = name,username = username, password = password)
            CoroutineScope(Dispatchers.IO).launch {
                offlineVaultsRepository.insertVault(
                    vault
                )
            }
            Toast.makeText(
                context, "成功儲存", Toast.LENGTH_LONG
            ).show()
        }

         fun getAllVaults(): Flow<List<Vault>> {
             return offlineVaultsRepository.getAllVaults()
        }
    }
}