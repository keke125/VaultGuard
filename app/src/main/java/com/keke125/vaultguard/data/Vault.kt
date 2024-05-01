package com.keke125.vaultguard.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "vault")
@Parcelize
data class Vault(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String,
    val username: String,
    val password: String,
) : Parcelable
