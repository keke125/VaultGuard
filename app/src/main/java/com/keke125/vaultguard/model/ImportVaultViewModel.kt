package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.service.FileService
import java.io.InputStream

class ImportVaultViewModel(
    private val vaultsRepository: VaultsRepository,
    private val foldersRepository: FoldersRepository,
    private val fileService: FileService
) : ViewModel() {
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
}
