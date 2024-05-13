package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.service.FileService
import java.io.InputStream

class ImportVaultViewModel(
    private val vaultsRepository: VaultsRepository, private val fileService: FileService
) : ViewModel() {
    fun importVaultFromGPM(inputStream: InputStream): List<Vault>? {
        return fileService.readCsvFromGPM(inputStream)
    }

    fun importVaultFromVG(inputStream: InputStream): List<Vault>? {
        return fileService.readJsonFromVG(inputStream)
    }

    suspend fun saveVaults(vaults: List<Vault>) {
        vaultsRepository.insertVaults(vaults)
    }
}
