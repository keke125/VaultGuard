package com.keke125.vaultguard.data

import kotlinx.coroutines.flow.Flow

interface FoldersRepository {
    fun getAllFolders(): Flow<List<Folder>>

    fun getFolderByUid(uid: Int): Flow<Folder>

    suspend fun insertFolder(folder: Folder)

    suspend fun insertFolders(folders: List<Folder>)

    suspend fun deleteFolder(folder: Folder)

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolders(folders: List<Folder>)
}