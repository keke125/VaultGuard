package com.keke125.vaultguard.screen

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.keke125.vaultguard.activity.LoginActivity
import com.keke125.vaultguard.model.AuthViewModel

/**
 * Shows [content] only while the user is authenticated and the session has not timed out.
 *
 * The auth state is re-read from [AuthViewModel] every time the host becomes resumed, so a
 * session that expired in the background is locked as soon as the user returns. Whenever the
 * host is resumed but not authenticated, [LoginActivity] is launched (once per resume, since
 * launching it pauses the host).
 */
@Composable
fun RequireAuthentication(
    authViewModel: AuthViewModel,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val authUiState by authViewModel.authUiState.collectAsState()
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val isResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        authViewModel.refreshAuthStatus()
    }

    LaunchedEffect(authUiState.isAuthenticated, isResumed) {
        if (isResumed && !authUiState.isAuthenticated) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    if (authUiState.isAuthenticated) {
        content()
    }
}
