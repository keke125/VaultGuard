package com.keke125.vaultguard.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AppDBContainer {
    val vaultsRepository: VaultsRepository
    val auth: FirebaseAuth
}

class AppDBDataContainer(private val context: Context) : AppDBContainer {
    override val vaultsRepository: VaultsRepository by lazy {
        OfflineVaultsRepository(AppDB.getDatabase(context).vaultDAO())
    }
    override val auth: FirebaseAuth = Firebase.auth

}