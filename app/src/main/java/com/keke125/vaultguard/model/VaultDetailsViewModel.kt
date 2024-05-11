package com.keke125.vaultguard.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.VaultDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class VaultDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val vaultsRepository: VaultsRepository,
) : ViewModel() {
    private val vaultId: Int = checkNotNull(savedStateHandle[VaultDetailsDestination.VAULTID])

    val uiState: StateFlow<VaultDetailsUiState> =
        vaultsRepository.getVaultByUid(vaultId).filterNotNull().map {
            VaultDetailsUiState(vaultDetails = it.toVaultDetails())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = VaultDetailsUiState()
        )

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

fun Vault.toVaultDetailsUiState(): VaultDetailsUiState = VaultDetailsUiState(
    vaultDetails = this.toVaultDetails()
)