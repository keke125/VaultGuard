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

    @Query("SELECT * FROM vault WHERE LOWER(name) LIKE '%' || LOWER(:keyword) || '%' OR LOWER(username) LIKE '%' || LOWER(:keyword) || '%' OR LOWER(urlList) LIKE '%' || LOWER(:keyword) || '%' ")
    fun getAllFiltered(keyword: String): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE uid IN (:vaultIds)")
    fun loadAllByIds(vaultIds: IntArray): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE uid = :uid")
    fun findById(uid: Int): Flow<Vault>

    @Query("SELECT * FROM vault WHERE name LIKE '%' || :name || '%'")
    fun findByName(name: String): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE folderUid = :folderUid or (folderUid is null and :folderUid is null)")
    fun findByFolderUid(folderUid: Int?): Flow<List<Vault>>

    @Query("SELECT * FROM vault WHERE (LOWER(name) LIKE '%' || LOWER(:keyword) || '%' OR LOWER(username) LIKE '%' || LOWER(:keyword) || '%' OR LOWER(urlList) LIKE '%' || LOWER(:keyword) || '%') and (folderUid = :folderUid or (folderUid is null and :folderUid is null))")
    fun getAllFilteredByFolderUid(keyword: String, folderUid: Int?): Flow<List<Vault>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg vaults: Vault)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(vaults: List<Vault>)

    @Insert
    suspend fun insert(vault: Vault)

    @Delete
    suspend fun delete(vault: Vault)

    @Update
    suspend fun update(vault: Vault)

    @Delete
    suspend fun deleteList(vaults: List<Vault>)
}