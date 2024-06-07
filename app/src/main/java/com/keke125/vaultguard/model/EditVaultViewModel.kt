package com.keke125.vaultguard.model

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.EditVaultDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class EditVaultViewModel(
    savedStateHandle: SavedStateHandle, private val vaultsRepository: VaultsRepository, private val foldersRepository: FoldersRepository
) : ViewModel() {

    var vaultUiState by mutableStateOf(VaultDetailsUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[EditVaultDestination.VAULTID])

    var foldersUiState: StateFlow<FoldersUiState> = foldersRepository.getAllFolders().map {
        FoldersUiState(folderList = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = FoldersUiState()
    )

    val folderName = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            vaultUiState = vaultsRepository.getVaultByUid(itemId).filterNotNull().first()
                .toVaultDetailsUiState()
            if (vaultUiState.vaultDetails.folderUid != null){
                foldersRepository.getFolderByUid(vaultUiState.vaultDetails.folderUid!!)
                    .collect { folder ->
                        folderName.value = folder.name
                    }
            }else{
                folderName.value = null
            }
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

    fun updateFolderName(name: String) {
        folderName.value = name
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}