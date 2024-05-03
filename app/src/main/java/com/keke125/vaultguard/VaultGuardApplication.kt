package com.keke125.vaultguard

import android.app.Application
import com.keke125.vaultguard.data.AppDBContainer
import com.keke125.vaultguard.data.AppDBDataContainer

class VaultGuardApplication : Application() {
    lateinit var container: AppDBContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDBDataContainer(this)
    }
}