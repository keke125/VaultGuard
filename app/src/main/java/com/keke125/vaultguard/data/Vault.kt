package com.keke125.vaultguard.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Entity(tableName = "vault")
@Parcelize
@TypeConverters(Converters::class)
data class Vault(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String,
    val username: String,
    val password: String,
    val urlList: List<String>
) : Parcelable
