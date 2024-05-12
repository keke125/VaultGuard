package com.keke125.vaultguard.service

import com.google.gson.Gson
import com.keke125.vaultguard.data.Vault


class FileService {
    fun exportToJson(vaults: List<Vault>): String? {
        val gson = Gson()
        val json = gson.toJson(vaults)
        return json
    }
}