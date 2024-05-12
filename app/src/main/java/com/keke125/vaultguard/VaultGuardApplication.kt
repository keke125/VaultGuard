package com.keke125.vaultguard

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.keke125.vaultguard.data.AppDBContainer
import com.keke125.vaultguard.data.AppDBContainerImpl
import com.keke125.vaultguard.service.AuthService
import com.keke125.vaultguard.service.AuthServiceImpl
import com.keke125.vaultguard.service.FileService
import com.keke125.vaultguard.service.KeyService

class VaultGuardApplication : Application() {
    lateinit var container: AppDBContainer
    lateinit var authService: AuthService
    lateinit var keyService: KeyService
    lateinit var fileService: FileService

    override fun onCreate() {
        super.onCreate()
        keyService = KeyService()
        container = AppDBContainerImpl(this,keyService)
        authService = AuthServiceImpl(Firebase.auth)
        fileService = FileService()
    }
}