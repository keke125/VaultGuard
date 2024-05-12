package com.keke125.vaultguard.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.keke125.vaultguard.service.KeyService

interface AppDBContainer {
    val vaultsRepository: VaultsRepository
    val auth: FirebaseAuth
}

class AppDBContainerImpl(private val context: Context, private val keyService: KeyService) :
    AppDBContainer {
    override val vaultsRepository: VaultsRepository by lazy {
        VaultsRepositoryImpl(AppDB.getDatabase(context, keyService).vaultDAO())
    }
    override val auth: FirebaseAuth = Firebase.auth

}