package com.keke125.vaultguard.service

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class KeyService {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    private val aesKeyAlias = "VaultGuardAesKey"

    private fun getAesKey(): SecretKey {
        if (keyStore.containsAlias(aesKeyAlias)) {
            return keyStore.getKey(aesKeyAlias, null) as SecretKey
        }

        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            aesKeyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setKeySize(256)
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            build()
        }
        keyGenerator.init(parameterSpec)

        return keyGenerator.generateKey()
    }

    fun encrypt(data: String): String {
        val plaintext: ByteArray = data.toByteArray()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getAesKey())
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv
        return Base64.encodeToString(iv, Base64.DEFAULT) + ":" + Base64.encodeToString(
            ciphertext, Base64.DEFAULT
        )
    }

    fun decrypt(data: String): String {
        val ciphertext: List<String> = data.split(":")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = Base64.decode(ciphertext[0], Base64.DEFAULT)
        val cipherText: ByteArray = Base64.decode(ciphertext[1], Base64.DEFAULT)
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getAesKey(), gcmParameterSpec)
        val plaintext: ByteArray = cipher.doFinal(cipherText)
        return plaintext.toString(Charsets.UTF_8)
    }
}