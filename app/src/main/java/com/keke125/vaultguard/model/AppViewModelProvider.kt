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
            AddVaultViewModel(
                this.createSavedStateHandle(),
                vaultGuardApplication().container.vaultsRepository,
                vaultGuardApplication().container.foldersRepository
            )
        }

        initializer {
            VaultDetailsViewModel(
                this.createSavedStateHandle(),
                vaultGuardApplication().container.vaultsRepository,
                vaultGuardApplication().container.foldersRepository
            )
        }

        initializer {
            VaultViewModel(vaultGuardApplication().container.vaultsRepository)
        }

        initializer {
            EditVaultViewModel(
                this.createSavedStateHandle(),
                vaultGuardApplication().container.vaultsRepository,
                vaultGuardApplication().container.foldersRepository
            )
        }

        initializer {
            SearchVaultViewModel(vaultGuardApplication().container.vaultsRepository)
        }

        initializer {
            ExportVaultViewModel(
                vaultGuardApplication().container.vaultsRepository,
                vaultGuardApplication().fileService,
                vaultGuardApplication().passwordService
            )
        }

        initializer {
            ImportVaultViewModel(
                vaultGuardApplication().container.vaultsRepository,
                vaultGuardApplication().fileService
            )
        }

        initializer {
            BiometricAuthSettingViewModel(vaultGuardApplication().userPreferencesRepository)
        }

        initializer {
            AuthViewModel(vaultGuardApplication().passwordService)
        }

        initializer {
            DeleteVaultsViewModel(
                vaultGuardApplication().container.vaultsRepository,
                vaultGuardApplication().container.foldersRepository,
                vaultGuardApplication().passwordService
            )
        }

        initializer {
            ChangeMainPasswordViewModel(vaultGuardApplication().passwordService)
        }

        initializer {
            FolderViewModel(vaultGuardApplication().container.foldersRepository)
        }

        initializer {
            AddFolderViewModel(vaultGuardApplication().container.foldersRepository)
        }

        initializer {
            EditFolderViewModel(
                this.createSavedStateHandle(), vaultGuardApplication().container.foldersRepository
            )
        }

        initializer {
            FolderDetailsViewModel(
                this.createSavedStateHandle(),
                vaultGuardApplication().container.foldersRepository,
                vaultGuardApplication().container.vaultsRepository
            )
        }
    }
}

fun CreationExtras.vaultGuardApplication(): VaultGuardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VaultGuardApplication)