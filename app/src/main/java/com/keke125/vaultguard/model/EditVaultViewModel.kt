package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.EditVaultDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditVaultViewModel(
    savedStateHandle: SavedStateHandle, private val vaultsRepository: VaultsRepository
) : ViewModel() {

    var vaultUiState by mutableStateOf(VaultDetailsUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[EditVaultDestination.VAULTID])

    init {
        viewModelScope.launch {
            vaultUiState = vaultsRepository.getVaultByUid(itemId).filterNotNull().first()
                .toVaultDetailsUiState()
        }
    }

    suspend fun updateVault() {
        vaultsRepository.updateVault(vaultUiState.vaultDetails.toVault())
    }

    fun updateUiState(vaultDetails: VaultDetails) {
        vaultUiState = VaultDetailsUiState(vaultDetails)
    }
}