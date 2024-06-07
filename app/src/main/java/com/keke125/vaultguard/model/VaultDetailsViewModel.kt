package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.VaultDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val vaultsRepository: VaultsRepository,
    private val foldersRepository: FoldersRepository
) : ViewModel() {
    private val vaultId: Int = checkNotNull(savedStateHandle[VaultDetailsDestination.VAULTID])

    val uiState: StateFlow<VaultDetailsUiState> =
        vaultsRepository.getVaultByUid(vaultId).filterNotNull().map { vault ->
            val vaultDetailsUiState = VaultDetailsUiState(vaultDetails = vault.toVaultDetails())

            viewModelScope.launch {
                if (vault.folderUid != null) {
                    foldersRepository.getFolderByUid(vault.folderUid).collect { folder ->
                        folderUiState = FolderUiState(folder = folder)
                    }
                } else {
                    folderUiState = FolderUiState(folder = null)
                }
            }

            vaultDetailsUiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = VaultDetailsUiState()
        )

    var folderUiState by mutableStateOf(FolderUiState())
        private set


    suspend fun deleteItem() {
        vaultsRepository.deleteVault(uiState.value.vaultDetails.toVault())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class VaultDetailsUiState(
    var vaultDetails: VaultDetails = VaultDetails()
)

fun Vault.toVaultDetailsUiState(): VaultDetailsUiState {
    return VaultDetailsUiState(
        vaultDetails = this.toVaultDetails()
    )
}