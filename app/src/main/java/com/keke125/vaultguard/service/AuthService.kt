package com.keke125.vaultguard.service

import android.app.Activity
import android.content.Context
import com.keke125.vaultguard.data.User
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    suspend fun authenticateWithEmailAndPassword(
        email: String,
        password: String,
        activity: Activity,
        context: Context,
        openAndPopUp: () -> Unit
    )

    suspend fun sendRecoveryEmail(email: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun signupWithEmailAndPassword(
        email: String,
        password: String,
        context: Context,
        activity: Activity,
        openAndPopUp: () -> Unit
    )
}