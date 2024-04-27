package com.keke125.vaultguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Vault::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun vaultDAO(): VaultDAO

    companion object {
        @Volatile
        private var Instance: AppDB? = null

        fun getDatabase(context: Context): AppDB {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDB::class.java, "app_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}