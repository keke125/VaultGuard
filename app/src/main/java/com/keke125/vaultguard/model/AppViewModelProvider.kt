package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.keke125.vaultguard.VaultGuardApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddVaultViewModel(vaultGuardApplication().container.vaultsRepository)
        }
    }
}

fun CreationExtras.vaultGuardApplication(): VaultGuardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VaultGuardApplication)