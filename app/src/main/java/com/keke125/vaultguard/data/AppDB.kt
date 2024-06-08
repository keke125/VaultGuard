package com.keke125.vaultguard.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keke125.vaultguard.service.KeyService
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.security.SecureRandom


@Database(
    version = 9,
    entities = [Vault::class,Folder::class],
    autoMigrations = [
        AutoMigration (from = 8, to = 9)
    ],
    exportSchema = true
)
abstract class AppDB : RoomDatabase() {
    abstract fun vaultDAO(): VaultDAO

    abstract fun folderDAO(): FolderDAO

    companion object {
        @Volatile
        private var Instance: AppDB? = null

        fun getDatabase(context: Context, keyService: KeyService): AppDB {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                System.loadLibrary("sqlcipher")
                val sharedPref = context.getSharedPreferences(
                    "AppDB", Context.MODE_PRIVATE
                )
                var dbPassword: String? = null
                if (sharedPref != null) {
                    if (sharedPref.contains("DB_PASSWORD")) {
                        val encryptedPassword = sharedPref.getString("DB_PASSWORD", "")
                        dbPassword = encryptedPassword?.let { keyService.decrypt(it, context) }
                    } else {
                        val charset = mutableListOf<Char>()
                        charset.addAll('1'..'9')
                        charset.addAll('a'..'z')
                        charset.addAll('A'..'Z')
                        dbPassword = (1..32).joinToString(separator = "") {
                            charset[SecureRandom().nextInt(charset.size)].toString()
                        }
                        val encryptedPassword = keyService.encrypt(dbPassword, context)
                        with(sharedPref.edit()) {
                            putString("DB_PASSWORD", encryptedPassword)
                            apply()
                        }
                    }
                }
                val passphrase = dbPassword?.toByteArray()
                val factory = SupportOpenHelperFactory(passphrase)
                Room.databaseBuilder(context, AppDB::class.java, "app_db")
                    .openHelperFactory(factory).fallbackToDestructiveMigration().build()
                    .also { Instance = it }
            }
        }
    }
}