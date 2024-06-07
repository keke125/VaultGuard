package com.keke125.vaultguard.data

import android.content.Context
import com.keke125.vaultguard.service.KeyService

interface AppDBContainer {
    val vaultsRepository: VaultsRepository
    val foldersRepository: FoldersRepository
}

class AppDBContainerImpl(private val context: Context, private val keyService: KeyService) :
    AppDBContainer {
    override val vaultsRepository: VaultsRepository by lazy {
        VaultsRepositoryImpl(AppDB.getDatabase(context, keyService).vaultDAO())
    }

    override val foldersRepository: FoldersRepository by lazy {
        FoldersRepositoryImpl(AppDB.getDatabase(context, keyService).folderDAO())
    }

}