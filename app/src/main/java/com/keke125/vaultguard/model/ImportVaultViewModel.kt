package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.service.FileService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.InputStream

class ImportVaultViewModel(
    private val vaultsRepository: VaultsRepository,
    private val foldersRepository: FoldersRepository,
    private val fileService: FileService
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

    fun importVaultFromGPM(inputStream: InputStream): List<Vault>? {
        return fileService.readCsvFromGPM(inputStream)
    }

    fun importVaultAndFolderFromVG(inputStream: InputStream): Pair<List<Vault>, List<Folder>>? {
        return fileService.readJsonFromVG(inputStream)
    }

    suspend fun saveVaults(vaults: List<Vault>) {
        vaultsRepository.insertVaults(vaults)
    }

    suspend fun saveFolders(folders: List<Folder>) {
        foldersRepository.insertFolders(folders)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
