package com.keke125.vaultguard.data

import kotlinx.coroutines.flow.Flow

interface VaultsRepository {
    fun getAllVaults(): Flow<List<Vault>>

    fun getAllVaultsFiltered(keyword: String): Flow<List<Vault>>

    fun getVaultByUid(uid: Int): Flow<Vault?>

    fun getVaultsByFolderUid(folderUid: Int?): Flow<List<Vault>>

    fun getAllVaultsFilteredByFolderUid(keyword: String, folderUid: Int?): Flow<List<Vault>>

    suspend fun insertVault(vault: Vault)

    suspend fun insertVaults(vaults: List<Vault>)

    suspend fun deleteVault(vault: Vault)

    suspend fun updateVault(vault: Vault)

    suspend fun deleteVaults(vaults: List<Vault>)
}