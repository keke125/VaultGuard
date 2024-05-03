package com.keke125.vaultguard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDAO {
    @Query("SELECT * FROM vault")
    fun getAll(): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE uid IN (:vaultIds)")
    fun loadAllByIds(vaultIds: IntArray): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE uid = :uid")
    fun findById(uid: Int): Flow<Vault>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg vaults: Vault)

    @Insert
    suspend fun insert(vault: Vault)

    @Delete
    suspend fun delete(vault: Vault)

    @Update
    suspend fun update(vault: Vault)
}