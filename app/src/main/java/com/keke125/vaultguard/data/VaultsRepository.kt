package com.keke125.vaultguard.data

import kotlinx.coroutines.flow.Flow

interface VaultsRepository {
    fun getAllVaults(): Flow<List<Vault>>

    fun getVault(uid: Int): Flow<Vault?>

    suspend fun insertVault(vault: Vault)

    suspend fun deleteVault(vault: Vault)

    suspend fun updateVault(vault: Vault)
}