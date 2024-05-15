package com.keke125.vaultguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "vault")
@TypeConverters(Converters::class)
data class Vault(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String,
    val username: String,
    val password: String,
    val urlList: List<String>,
    val notes: String
)
