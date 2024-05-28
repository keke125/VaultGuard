package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.keke125.vaultguard.VaultGuardApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddVaultViewModel(vaultGuardApplication().container.vaultsRepository)
        }

        initializer {
            VaultDetailsViewModel(
                this.createSavedStateHandle(), vaultGuardApplication().container.vaultsRepository
            )
        }

        initializer {
            VaultViewModel(vaultGuardApplication().container.vaultsRepository)
        }

        initializer {
            EditVaultViewModel(
                this.createSavedStateHandle(), vaultGuardApplication().container.vaultsRepository
            )
        }

        initializer {
            SearchVaultViewModel(vaultGuardApplication().container.vaultsRepository)
        }

        initializer {
            ExportVaultViewModel(vaultGuardApplication().container.vaultsRepository,vaultGuardApplication().fileService)
        }

        initializer {
            ImportVaultViewModel(vaultGuardApplication().container.vaultsRepository,vaultGuardApplication().fileService)
        }

        initializer {
            BiometricAuthSettingViewModel(vaultGuardApplication().userPreferencesRepository)
        }

        initializer {
            AuthViewModel(vaultGuardApplication().passwordService)
        }

        initializer {
            DeleteVaultsViewModel(vaultGuardApplication().container.vaultsRepository)
        }
    }
}

fun CreationExtras.vaultGuardApplication(): VaultGuardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VaultGuardApplication)