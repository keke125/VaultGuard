package com.keke125.vaultguard

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.keke125.vaultguard.data.AppDBContainer
import com.keke125.vaultguard.data.AppDBContainerImpl
import com.keke125.vaultguard.data.UserPreferencesRepository
import com.keke125.vaultguard.service.FileService
import com.keke125.vaultguard.service.KeyService
import com.keke125.vaultguard.service.PasswordService

private const val USER_PREFERENCE_NAME = "user_preferences"
private val Context.userPreferenceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE_NAME
)

class VaultGuardApplication : Application() {
    lateinit var container: AppDBContainer
    private lateinit var keyService: KeyService
    lateinit var fileService: FileService
    lateinit var passwordService: PasswordService
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        keyService = KeyService()
        container = AppDBContainerImpl(this,keyService)
        fileService = FileService()
        userPreferencesRepository = UserPreferencesRepository(userPreferenceDataStore)
        passwordService = PasswordService(this)
    }
}