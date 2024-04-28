package com.keke125.vaultguard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDAO {
    @Query("SELECT * FROM vault")
    fun getAll(): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE uid IN (:vaultIds)")
    fun loadAllByIds(vaultIds: IntArray): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE name is :name")
    fun findByName(name: String): Flow<Vault?>

    @Query("SELECT * FROM vault WHERE uid = :uid")
    fun get(uid: Int): Flow<Vault?>

    @Insert
    suspend fun insertAll(vararg vaults: Vault)

    @Insert
    suspend fun insert(vault: Vault)

    @Delete
    suspend fun delete(vault: Vault)

    @Update
    suspend fun update(vault: Vault)
}