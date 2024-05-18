package com.keke125.vaultguard.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AuthPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val MAIN_PASSWORD_HASHED = stringPreferencesKey("main_password_hashed")
    }

    val mainPasswordHashed: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[MAIN_PASSWORD_HASHED] ?: ""
    }

    suspend fun saveAuthPreference(mainPasswordHashed: String) {
        dataStore.edit { preferences ->
            preferences[MAIN_PASSWORD_HASHED] = mainPasswordHashed
        }
    }
}