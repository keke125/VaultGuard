package com.keke125.vaultguard.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.FolderDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FolderDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val foldersRepository: FoldersRepository,
    vaultsRepository: VaultsRepository
) : ViewModel() {
    val folderId: Int = checkNotNull(savedStateHandle[FolderDetailsDestination.FOLDERID])

    val uiState: StateFlow<FolderDetailsUiState> =
        foldersRepository.getFolderByUid(folderId).filterNotNull().map {
            FolderDetailsUiState(folderDetails = it.toFolderDetails())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FolderDetailsUiState()
        )

    val vaultUiState: StateFlow<VaultUiState> = if (folderId == 0) {
        vaultsRepository.getVaultsByFolderUid(null).map { VaultUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = VaultUiState()
        )
    } else {
        vaultsRepository.getVaultsByFolderUid(folderId).map { VaultUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = VaultUiState()
        )
    }

    suspend fun deleteItem() {
        foldersRepository.deleteFolder(uiState.value.folderDetails.toFolder())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class FolderDetailsUiState(
    var folderDetails: FolderDetails = FolderDetails()
)

fun Folder.toFolderDetailsUiState(): FolderDetailsUiState = FolderDetailsUiState(
    folderDetails = this.toFolderDetails()
)