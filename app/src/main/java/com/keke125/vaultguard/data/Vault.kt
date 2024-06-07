package com.keke125.vaultguard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverters

@Entity(
    tableName = "vault",
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["uid"],
            childColumns = ["folderUid"])
    ]
)
@TypeConverters(Converters::class)
data class Vault(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String,
    val username: String,
    val password: String,
    val urlList: List<String>,
    val notes: String,
    val totp: String,
    @ColumnInfo(defaultValue = "")
    val createdDateTime: String,
    @ColumnInfo(defaultValue = "")
    val lastModifiedDateTime: String,
    @ColumnInfo(defaultValue = "null")
    val folderUid: Int?,
)
