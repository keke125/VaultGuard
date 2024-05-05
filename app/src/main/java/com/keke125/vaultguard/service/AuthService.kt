package com.keke125.vaultguard.service

import android.app.Activity
import android.content.Context
import com.keke125.vaultguard.data.User
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun signupWithEmailAndPassword(
        email: String, password: String, context: Context, activity: Activity
    )
}