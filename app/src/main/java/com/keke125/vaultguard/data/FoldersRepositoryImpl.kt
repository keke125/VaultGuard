package com.keke125.vaultguard.data

import kotlinx.coroutines.flow.Flow

class FoldersRepositoryImpl(private val folderDAO: FolderDAO) : FoldersRepository {

    override fun getAllFolders(): Flow<List<Folder>> = folderDAO.getAll()

    override fun getFolderByUid(uid: Int): Flow<Folder> = folderDAO.findById(uid)

    override suspend fun insertFolder(folder: Folder) = folderDAO.insert(folder)

    override suspend fun insertFolders(folders: List<Folder>) = folderDAO.insertList(folders)

    override suspend fun deleteFolder(folder: Folder) = folderDAO.delete(folder)

    override suspend fun updateFolder(folder: Folder) = folderDAO.update(folder)

    override suspend fun deleteFolders(folders: List<Folder>) = folderDAO.deleteList(folders)
}