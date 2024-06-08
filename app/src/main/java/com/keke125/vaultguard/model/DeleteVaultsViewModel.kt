package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.service.PasswordService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DeleteVaultsViewModel(
    private val vaultsRepository: VaultsRepository,
    private val foldersRepository: FoldersRepository,
    private val passwordService: PasswordService
) : ViewModel() {

    val vaultUiState: StateFlow<VaultUiState> =
        vaultsRepository.getAllVaults().map { VaultUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = VaultUiState()
        )

    val folderUiState: StateFlow<FoldersUiState> =
        foldersRepository.getAllFolders().map { FoldersUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FoldersUiState()
        )

    var deleteVaultsUiState by mutableStateOf(DeleteVaultsUiState())
        private set

    suspend fun deleteVaultsAndFolders(vaults: List<Vault>, folders: List<Folder>) {
        vaultsRepository.deleteVaults(vaults)
        foldersRepository.deleteFolders(folders)
    }

    fun updateUiState(deleteVaults: DeleteVaults) {
        deleteVaultsUiState = DeleteVaultsUiState(deleteVaults = deleteVaults)
    }

    fun checkMainPassword(password: String): Boolean {
        return passwordService.validatePassword(password)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class DeleteVaultsUiState(
    val deleteVaults: DeleteVaults = DeleteVaults()
)

data class DeleteVaults(
    var password: String = "",
)