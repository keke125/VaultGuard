package com.keke125.vaultguard.data

interface VaultsRepository {
    fun getAllVaults(): List<Vault>

    fun getVault(uid: Int): Vault?

    suspend fun insertVault(vault: Vault)

    suspend fun deleteVault(vault: Vault)

    suspend fun updateVault(vault: Vault)
}