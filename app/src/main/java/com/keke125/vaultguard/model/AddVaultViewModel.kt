package com.keke125.vaultguard.model

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class AddVaultViewModel(private val vaultsRepository: VaultsRepository) : ViewModel() {
    var vaultUiState by mutableStateOf(AddVaultUiState())
        private set

    fun updateUiState(vaultDetails: VaultDetails) {
        vaultUiState = AddVaultUiState(vaultDetails = vaultDetails)
    }

    suspend fun saveVault() {
        val timeStamp: String
        if (Build.VERSION.SDK_INT >= 26) {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            timeStamp = LocalDateTime.now().format(formatter).toString()
        } else {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            timeStamp = simpleDateFormat.format(Date())
        }
        updateUiState(vaultUiState.vaultDetails.copy(createdDateTime = timeStamp))
        updateUiState(vaultUiState.vaultDetails.copy(lastModifiedDateTime = timeStamp))
        vaultsRepository.insertVault(vaultUiState.vaultDetails.toVault())
    }
}

data class AddVaultUiState(
    val vaultDetails: VaultDetails = VaultDetails(),
)

data class VaultDetails(
    val uid: Int = 0,
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val urlList: List<String> = emptyList(),
    val notes: String = "",
    val totp: String = "",
    val createdDateTime: String = "",
    val lastModifiedDateTime: String = ""
)

fun VaultDetails.toVault(): Vault = Vault(
    uid = uid, name = name, username = username, password = password, urlList = urlList, notes = notes, totp = totp, createdDateTime = createdDateTime, lastModifiedDateTime = lastModifiedDateTime
)

fun Vault.toVaultDetails(): VaultDetails = VaultDetails(
    uid = uid, name = name, username = username, password = password, urlList = urlList, notes = notes, totp = totp, createdDateTime = createdDateTime, lastModifiedDateTime = lastModifiedDateTime
)