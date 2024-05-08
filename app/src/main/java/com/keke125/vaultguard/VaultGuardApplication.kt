package com.keke125.vaultguard

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.keke125.vaultguard.data.AppDBContainer
import com.keke125.vaultguard.data.AppDBDataContainer
import com.keke125.vaultguard.service.AuthService
import com.keke125.vaultguard.service.AuthServiceImpl
import com.keke125.vaultguard.service.KeyService

class VaultGuardApplication : Application() {
    lateinit var container: AppDBContainer
    lateinit var authService: AuthService
    lateinit var keyService: KeyService

    override fun onCreate() {
        super.onCreate()
        keyService = KeyService()
        container = AppDBDataContainer(this,keyService)
        authService = AuthServiceImpl(Firebase.auth)
    }
}