package com.keke125.vaultguard.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.keke125.vaultguard.data.Vault
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.nio.charset.StandardCharsets


class FileService {
    fun exportToJson(vaults: List<Vault>): String? {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(vaults)
        return json
    }

    fun readCsvFromGPM(inputStream: InputStream): List<Vault>? {
        try {
            val csvParser = CSVParser.parse(
                inputStream, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
            )
            val vaults = mutableListOf<Vault>()
            for (csvRecord in csvParser) {
                val name = csvRecord.get(0)
                val username = csvRecord.get(2)
                val password = csvRecord.get(3)
                val notes = csvRecord.get(4)
                val urlList = listOf(csvRecord.get(1))
                val vault = Vault(
                    name = name,
                    username = username,
                    password = password,
                    notes = notes,
                    urlList = urlList,
                    totp = ""
                )
                vaults.add(vault)
            }
            return vaults
        }catch (e: Exception){
            return null
        }
    }

    fun readJsonFromVG(inputStream: InputStream): List<Vault>? {
        try {
            val gson = Gson()
            val reader = JsonReader(inputStream.reader())
            val vaultListType = object : TypeToken<List<Vault>?>() {}
            val vaultsFromJson = gson.fromJson(reader,vaultListType)
            val vaults = mutableListOf<Vault>()
            if (vaultsFromJson != null) {
                for (vault in vaultsFromJson) {
                    val name = vault.name
                    val username = vault.username
                    val password = vault.password
                    val notes = vault.notes
                    val urlList = vault.urlList
                    val totp = vault.totp
                    val newVault = Vault(
                        name = name,
                        username = username,
                        password = password,
                        notes = notes,
                        urlList = urlList,
                        totp = totp
                    )
                    vaults.add(newVault)
                }
                return vaults
            }
            else{
                return null
            }
        }catch (e: Exception){
            return null
        }
    }
}