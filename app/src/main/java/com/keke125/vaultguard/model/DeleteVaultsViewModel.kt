package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DeleteVaultsViewModel(private val vaultsRepository: VaultsRepository) : ViewModel() {

    val vaultUiState: StateFlow<VaultUiState> =
        vaultsRepository.getAllVaults().map { VaultUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = VaultUiState()
        )

    suspend fun deleteVault(vaults: List<Vault>) {
        vaultsRepository.deleteVaults(vaults)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}