package com.keke125.vaultguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault")
data class Vault(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String,
    val username: String,
    val password: String,
)
