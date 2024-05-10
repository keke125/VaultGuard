package com.keke125.vaultguard.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromUrlListToString(urlList: List<String>?): String? {
        return urlList?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun fromStringToUrlList(string: String?): List<String> {
        return Gson().fromJson(string, Array<String>::class.java).toList()
    }
}