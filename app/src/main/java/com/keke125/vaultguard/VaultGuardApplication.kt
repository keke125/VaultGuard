package com.keke125.vaultguard

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.keke125.vaultguard.data.AppDBContainer
import com.keke125.vaultguard.data.AppDBDataContainer
import com.keke125.vaultguard.service.AuthService
import com.keke125.vaultguard.service.AuthServiceImpl

class VaultGuardApplication : Application() {
    lateinit var container: AppDBContainer
    lateinit var authService: AuthService

    override fun onCreate() {
        super.onCreate()
        container = AppDBDataContainer(this)
        authService = AuthServiceImpl(Firebase.auth)
    }
}