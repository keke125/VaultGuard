package com.keke125.vaultguard.model

import android.os.Build
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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
        val timeStamp: String
        if (Build.VERSION.SDK_INT >= 26) {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            timeStamp = LocalDateTime.now().format(formatter).toString()
        } else {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            timeStamp = simpleDateFormat.format(Date())
        }
        updateUiState(
            vaultUiState.vaultDetails.copy(
                lastModifiedDateTime = timeStamp
            )
        )
        vaultsRepository.updateVault(vaultUiState.vaultDetails.toVault())
    }

    fun updateUiState(vaultDetails: VaultDetails) {
        vaultUiState = VaultDetailsUiState(vaultDetails)
    }
}