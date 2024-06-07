package com.keke125.vaultguard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDAO {
    @Query("SELECT * FROM folder")
    fun getAll(): Flow<List<Folder>>

    @Query("SELECT * FROM folder WHERE name LIKE '%' || :name || '%'")
    fun findByName(name: String): Flow<List<Folder>>

    @Query("SELECT * FROM folder WHERE uid = :uid")
    fun findById(uid: Int): Flow<Folder>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg folders: Folder)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(folders: List<Folder>)

    @Insert
    suspend fun insert(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun deleteList(folders: List<Folder>)
}