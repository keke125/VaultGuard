package com.keke125.vaultguard.service

import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.Vault
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class FileService {
    fun exportToJson(vaults: List<Vault>, folders: List<Folder>): String? {
        val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val vaultsAndFolders = Pair(vaults, folders)
        val json = gson.toJson(vaultsAndFolders)
        return json
    }

    fun readCsvFromGPM(inputStream: InputStream): List<Vault>? {
        try {
            val csvParser = CSVParser.parse(
                inputStream,
                StandardCharsets.UTF_8,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase()
            )
            val vaults = mutableListOf<Vault>()
            for (csvRecord in csvParser) {
                val name = csvRecord.get(0)
                val username = csvRecord.get(2)
                val password = csvRecord.get(3)
                val notes = csvRecord.get(4)
                val urlList = listOf(csvRecord.get(1))
                val timeStamp: String
                if (Build.VERSION.SDK_INT >= 26) {
                    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    timeStamp = LocalDateTime.now().format(formatter).toString()
                } else {
                    val simpleDateFormat =
                        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                    timeStamp = simpleDateFormat.format(Date())
                }
                val vault = Vault(
                    name = name,
                    username = username,
                    password = password,
                    notes = notes,
                    urlList = urlList,
                    totp = "",
                    createdDateTime = timeStamp,
                    lastModifiedDateTime = timeStamp,
                    folderUid = null
                )
                vaults.add(vault)
            }
            return vaults
        } catch (e: Exception) {
            return null
        }
    }

    fun readJsonFromVG(inputStream: InputStream): Pair<List<Vault>, List<Folder>>? {
        try {
            val gson = Gson()
            val reader = JsonReader(inputStream.reader())
            val listType = object : TypeToken<Pair<List<Vault>, List<Folder>>>() {}
            val vaultsAndFoldersFromJson = gson.fromJson(reader, listType)
            val vaultsFromJson = vaultsAndFoldersFromJson.first
            val foldersFromJson = vaultsAndFoldersFromJson.second
            val vaults = mutableListOf<Vault>()
            val folders = mutableListOf<Folder>()
            if (vaultsFromJson.isEmpty() and foldersFromJson.isEmpty()) {
                return null
            }
            if (vaultsFromJson.isNotEmpty()) {
                for (vault in vaultsFromJson) {
                    val name = vault.name
                    val username = vault.username
                    val password = vault.password
                    val notes = vault.notes
                    val urlList = vault.urlList
                    val totp = vault.totp
                    val createdDateTime = vault.createdDateTime
                    val lastModifiedDateTime = vault.lastModifiedDateTime
                    val folderUid = vault.folderUid
                    val newVault = Vault(
                        name = name,
                        username = username,
                        password = password,
                        notes = notes,
                        urlList = urlList,
                        totp = totp,
                        createdDateTime = createdDateTime,
                        lastModifiedDateTime = lastModifiedDateTime,
                        folderUid = folderUid
                    )
                    vaults.add(newVault)
                }
            }
            if (foldersFromJson.isNotEmpty()) {
                for (folder in foldersFromJson) {
                    val name = folder.name
                    val uid = folder.uid
                    val newFolder = Folder(
                        name = name,
                        uid = uid
                    )
                    folders.add(newFolder)
                }
            }
            return Pair(vaults, folders)
        } catch (e: Exception) {
            return null
        }
    }
}