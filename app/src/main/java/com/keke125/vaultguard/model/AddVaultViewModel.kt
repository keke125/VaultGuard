package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddVaultViewModel(private val vaultsRepository: VaultsRepository) : ViewModel() {
    var vaultUiState by mutableStateOf(AddVaultUiState())
        private set

    var deleteVaultsUiState: StateFlow<DeleteVaultUiState> =
        vaultsRepository.getVaultByNameAndUsername("","").map {
            DeleteVaultUiState(vault = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DeleteVaultUiState()
        )

    fun updateUiState(vaultDetails: VaultDetails) {
        vaultUiState = AddVaultUiState(vaultDetails = vaultDetails)
    }

    fun updateNameAndUsername(name: String, username: String) {
        deleteVaultsUiState = vaultsRepository.getVaultByNameAndUsername(name,username).map {
            DeleteVaultUiState(vault = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = DeleteVaultUiState()
        )
    }

    suspend fun saveVault() {
        vaultsRepository.insertVault(vaultUiState.vaultDetails.toVault())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
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
    val totp: String = ""
)

fun VaultDetails.toVault(): Vault = Vault(
    uid = uid, name = name, username = username, password = password, urlList = urlList, notes = notes, totp = totp
)

fun Vault.toVaultDetails(): VaultDetails = VaultDetails(
    uid = uid, name = name, username = username, password = password, urlList = urlList, notes = notes, totp = totp
)

data class DeleteVaultUiState(val vault: Vault? = null)