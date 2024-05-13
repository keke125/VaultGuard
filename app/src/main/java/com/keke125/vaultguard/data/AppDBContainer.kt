package com.keke125.vaultguard.data

import android.content.Context
import com.keke125.vaultguard.service.KeyService

interface AppDBContainer {
    val vaultsRepository: VaultsRepository
}

class AppDBContainerImpl(private val context: Context, private val keyService: KeyService) :
    AppDBContainer {
    override val vaultsRepository: VaultsRepository by lazy {
        VaultsRepositoryImpl(AppDB.getDatabase(context, keyService).vaultDAO())
    }

}