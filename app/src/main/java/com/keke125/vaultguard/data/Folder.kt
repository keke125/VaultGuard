package com.keke125.vaultguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String
)