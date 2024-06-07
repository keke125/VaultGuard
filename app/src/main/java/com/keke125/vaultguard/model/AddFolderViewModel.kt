package com.keke125.vaultguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.FoldersRepository

class AddFolderViewModel(private val foldersRepository: FoldersRepository) : ViewModel() {
    var folderUiState by mutableStateOf(AddFolderUiState())
        private set

    fun updateUiState(folderDetails: FolderDetails) {
        folderUiState = AddFolderUiState(folderDetails = folderDetails)
    }

    suspend fun saveFolder() {
        foldersRepository.insertFolder(folderUiState.folderDetails.toFolder())
    }
}

data class AddFolderUiState(
    val folderDetails: FolderDetails = FolderDetails(),
)

data class FolderDetails(
    val uid: Int = 0, val name: String = ""
)

fun FolderDetails.toFolder(): Folder = Folder(
    uid = uid, name = name
)

fun Folder.toFolderDetails(): FolderDetails = FolderDetails(
    uid = uid, name = name
)