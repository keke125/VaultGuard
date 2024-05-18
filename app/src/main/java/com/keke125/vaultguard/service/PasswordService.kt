package com.keke125.vaultguard.service

import android.os.Build
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class PasswordService {

    fun generatePasswordHash(password: String): String {
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

    fun validatePassword(password: String, hashedPassword: String): Boolean {
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
        return diff == 0
    }

}