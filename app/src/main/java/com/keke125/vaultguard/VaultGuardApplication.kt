package com.keke125.vaultguard

import android.app.Application
import com.keke125.vaultguard.data.AppDBContainer
import com.keke125.vaultguard.data.AppDBContainerImpl
import com.keke125.vaultguard.service.FileService
import com.keke125.vaultguard.service.KeyService

class VaultGuardApplication : Application() {
    lateinit var container: AppDBContainer
    lateinit var keyService: KeyService
    lateinit var fileService: FileService

    override fun onCreate() {
        super.onCreate()
        keyService = KeyService()
        container = AppDBContainerImpl(this,keyService)
        fileService = FileService()
    }
}