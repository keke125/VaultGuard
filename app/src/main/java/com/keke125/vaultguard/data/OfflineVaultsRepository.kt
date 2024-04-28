package com.keke125.vaultguard.data

import kotlinx.coroutines.flow.Flow

class OfflineVaultsRepository(private val vaultDAO: VaultDAO) : VaultsRepository {
    override fun getAllVaults(): Flow<List<Vault>> = vaultDAO.getAll()

    override fun getVault(uid: Int): Flow<Vault?> = vaultDAO.get(uid)

    override suspend fun insertVault(vault: Vault) = vaultDAO.insert(vault)

    override suspend fun deleteVault(vault: Vault) = vaultDAO.delete(vault)

    override suspend fun updateVault(vault: Vault) = vaultDAO.update(vault)
}