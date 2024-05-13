package com.keke125.vaultguard.service

import com.google.gson.GsonBuilder
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

    fun readCsvFromGooglePasswordManager(inputStream: InputStream): List<Vault>? {
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
                    urlList = urlList
                )
                vaults.add(vault)
            }
            return vaults
        }catch (e: Exception){
            return null
        }
    }
}