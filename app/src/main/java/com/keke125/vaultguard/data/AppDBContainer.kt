package com.keke125.vaultguard.data

import android.content.Context

interface AppDBContainer {
    val vaultsRepository: VaultsRepository
}

class AppDBDataContainer(private val context: Context) : AppDBContainer {
    override val vaultsRepository: VaultsRepository by lazy {
        OfflineVaultsRepository(AppDB.getDatabase(context).vaultDAO())
    }
}