package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.screen.EditFolderDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditFolderViewModel(
    savedStateHandle: SavedStateHandle, private val foldersRepository: FoldersRepository
) : ViewModel() {

    var folderUiState by mutableStateOf(FolderDetailsUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[EditFolderDestination.FOLDERID])

    init {
        viewModelScope.launch {
            folderUiState = foldersRepository.getFolderByUid(itemId).filterNotNull().first()
                .toFolderDetailsUiState()
        }
    }

    suspend fun updateFolder() {
        foldersRepository.updateFolder(folderUiState.folderDetails.toFolder())
    }

    fun updateUiState(folderDetails: FolderDetails) {
        folderUiState = FolderDetailsUiState(folderDetails)
    }
}