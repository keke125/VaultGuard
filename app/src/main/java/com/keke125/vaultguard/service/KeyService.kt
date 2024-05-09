package com.keke125.vaultguard.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import java.security.spec.InvalidKeySpecException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec


class KeyService {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    private val aesKeyAlias = "VaultGuardAesKey"

    private fun getAesKey(context: Context): SecretKey {
        if (keyStore.containsAlias(aesKeyAlias)) {
            val factory = SecretKeyFactory.getInstance(
                keyStore.getKey(aesKeyAlias, null).algorithm, "AndroidKeyStore"
            )
            val keyInfo: KeyInfo
            try {
                keyInfo = factory.getKeySpec(
                    keyStore.getKey(aesKeyAlias, null) as SecretKey?, KeyInfo::class.java
                ) as KeyInfo
                if (Build.VERSION.SDK_INT < 31) {
                    if (keyInfo.isInsideSecureHardware) {
                        Log.d("VaultGuard", "Inside secure hardware")
                    } else {
                        Log.d("VaultGuard", "Outside secure hardware")
                    }
                } else {
                    Log.d("VaultGuard", "Security Level: " + keyInfo.securityLevel)
                }
            } catch (e: InvalidKeySpecException) {
                Log.d("VaultGuard", "Invalid key spec")
            }
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
            if (Build.VERSION.SDK_INT >= 28) {
                if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)) {
                    setIsStrongBoxBacked(true)
                    Log.d("VaultGuard", "Strong Box Available")
                }
            }
            build()
        }
        keyGenerator.init(parameterSpec)

        return keyGenerator.generateKey()
    }

    fun encrypt(data: String, context: Context): String {
        val plaintext: ByteArray = data.toByteArray()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getAesKey(context))
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv
        return Base64.encodeToString(iv, Base64.DEFAULT) + ":" + Base64.encodeToString(
            ciphertext, Base64.DEFAULT
        )
    }

    fun decrypt(data: String, context: Context): String {
        val ciphertext: List<String> = data.split(":")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = Base64.decode(ciphertext[0], Base64.DEFAULT)
        val cipherText: ByteArray = Base64.decode(ciphertext[1], Base64.DEFAULT)
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getAesKey(context), gcmParameterSpec)
        val plaintext: ByteArray = cipher.doFinal(cipherText)
        return plaintext.toString(Charsets.UTF_8)
    }
}