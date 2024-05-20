package com.keke125.vaultguard.service

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class PasswordService(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        "Auth", Context.MODE_PRIVATE
    )

    fun isSignup(): Boolean {
        return sharedPref.contains("PASSWORD_HASHED")
    }

    fun isAuthenticated(): Boolean {
        return sharedPref.getBoolean("IS_AUTHENTICATED", false)
    }

    fun updatePassword(password: String) {
        val passwordHashed = generatePasswordHash(password)
        with(sharedPref.edit()) {
            putString("PASSWORD_HASHED", passwordHashed)
            apply()
        }
    }

    private fun generatePasswordHash(password: String): String {
        val iterations = 10000
        val chars = password.toCharArray()
        val salt = getSalt()
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf: SecretKeyFactory = if (Build.VERSION.SDK_INT > 26) {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        } else {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        }
        val hash = skf.generateSecret(spec).encoded
        return if (Build.VERSION.SDK_INT > 26) {
            "PBKDF2WithHmacSHA256:$iterations:" + Base64.encodeToString(
                salt, Base64.DEFAULT
            ) + ":" + Base64.encodeToString(hash, Base64.DEFAULT)
        } else {
            "PBKDF2WithHmacSHA1:$iterations:" + Base64.encodeToString(
                salt, Base64.DEFAULT
            ) + ":" + Base64.encodeToString(hash, Base64.DEFAULT)
        }
    }

    private fun getSalt(): ByteArray {
        val secureRandom = SecureRandom()
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return salt
    }

    fun validatePassword(password: String): Boolean {
        val hashedPassword = sharedPref.getString("PASSWORD_HASHED", "")!!
        if (hashedPassword.isEmpty()) {
            return false
        }
        val hashedPasswordList: List<String> = hashedPassword.split(":")
        val hashAlg = hashedPasswordList[0]
        val iterations = hashedPasswordList[1].toInt()
        val salt = Base64.decode(hashedPasswordList[2], Base64.DEFAULT)
        val hash = Base64.decode(hashedPasswordList[3], Base64.DEFAULT)
        val spec = PBEKeySpec(
            password.toCharArray(), salt, iterations, hash.size * 8
        )
        val skf: SecretKeyFactory = SecretKeyFactory.getInstance(hashAlg)
        val testHash = skf.generateSecret(spec).encoded

        var diff = hash.size xor testHash.size
        var i = 0
        while (i < hash.size && i < testHash.size) {
            diff = diff or (hash[i].toInt() xor testHash[i].toInt())
            i++
        }
        if(diff == 0){
            with(sharedPref.edit()) {
                putBoolean("IS_AUTHENTICATED", true)
                apply()
            }
        }
        return diff == 0
    }

    fun logout(){
        with(sharedPref.edit()) {
            putBoolean("IS_AUTHENTICATED", false)
            apply()
        }
    }

    fun authenticateWithBiometric(isAuthenticationSuccessful: Boolean){
        with(sharedPref.edit()) {
            putBoolean("IS_AUTHENTICATED", isAuthenticationSuccessful)
            apply()
        }
    }

}