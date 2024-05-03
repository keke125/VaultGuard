package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository

class AddVaultViewModel(private val vaultsRepository: VaultsRepository) : ViewModel() {
    var vaultUiState by mutableStateOf(VaultUiState())
        private set

    fun updateUiState(vaultDetails: VaultDetails) {
        vaultUiState = VaultUiState(vaultDetails = vaultDetails)
    }

    suspend fun saveVault() {
        vaultsRepository.insertVault(vaultUiState.vaultDetails.toItem())
    }
}

data class VaultUiState(
    val vaultDetails: VaultDetails = VaultDetails(),
)

data class VaultDetails(
    val uid: Int = 0,
    val name: String = "",
    val username: String = "",
    val password: String = "",
)

fun VaultDetails.toItem(): Vault = Vault(
    uid = uid, name = name, username = username, password = password
)

fun Vault.toVaultUiState(): VaultUiState = VaultUiState(
    vaultDetails = this.toVaultDetails(),
)

fun Vault.toVaultDetails(): VaultDetails = VaultDetails(
    uid = uid, name = name, username = username, password = password
)