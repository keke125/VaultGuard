package com.keke125.vaultguard.data

import kotlinx.coroutines.flow.Flow

class VaultsRepositoryImpl(private val vaultDAO: VaultDAO) : VaultsRepository {

    override fun getAllVaults(): Flow<List<Vault>> = vaultDAO.getAll()

    override fun getAllVaultsFiltered(keyword: String): Flow<List<Vault>>  = vaultDAO.getAllFiltered(keyword)

    override fun getVaultByUid(uid: Int): Flow<Vault?> = vaultDAO.findById(uid)

    override suspend fun insertVault(vault: Vault) = vaultDAO.insert(vault)

    override suspend fun insertVaults(vaults: List<Vault>) = vaultDAO.insertList(vaults)

    override suspend fun deleteVault(vault: Vault) = vaultDAO.delete(vault)

    override suspend fun updateVault(vault: Vault) = vaultDAO.update(vault)

    override suspend fun deleteVaults(vaults: List<Vault>) = vaultDAO.deleteList(vaults)
}