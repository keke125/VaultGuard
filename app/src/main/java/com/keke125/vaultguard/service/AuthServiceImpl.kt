package com.keke125.vaultguard.service

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.keke125.vaultguard.data.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class AuthServiceImpl(
    private val auth: FirebaseAuth
) : AuthService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()
    override val hasUser: Boolean
        get() = auth.currentUser != null
    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.let { User(it.uid) } ?: User())
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()
    }

    override suspend fun signupWithEmailAndPassword(
        email: String, password: String, context: Context, activity: Activity
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener(activity) { task ->
                Toast.makeText(
                    context, task.localizedMessage, Toast.LENGTH_LONG
                ).show()
            }.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Toast.makeText(
                            context,
                            "Welcome ${user.email}, your account has been created.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
}