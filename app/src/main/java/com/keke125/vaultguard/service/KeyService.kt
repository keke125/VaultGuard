package com.keke125.vaultguard.service

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class KeyService {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    private val aesKeyAlias = "VaultGuardAesKey"
    /*
    fun getPubKey(context: Context): PublicKey? {
        return try {
            // KeyPair not store in keystore
            if(!ks.containsAlias(alias)){
                generateKey().public
            }else{
                ks.getCertificate(alias).publicKey
            }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
    */
    private fun getAesKey(): SecretKey {

        if(keyStore.containsAlias(aesKeyAlias)){
            return keyStore.getKey(aesKeyAlias, null) as SecretKey
        }

        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            aesKeyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setKeySize(256)
            build()
        }
        keyGenerator.init(parameterSpec)

        return keyGenerator.generateKey()
    }

    fun encrypt(data: String): ByteArray {
        val plaintext: ByteArray = data.toByteArray()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getAesKey())
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv
        return iv + ciphertext
    }

    fun decrypt(data: ByteArray): String {
        val cipherText: ByteArray = data
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = cipherText.sliceArray(0 until 12)
        val ciphertext: ByteArray = cipherText.sliceArray(12 until cipherText.size)
        cipher.init(Cipher.DECRYPT_MODE, getAesKey())
        val plaintext: ByteArray = cipher.doFinal(ciphertext)
        return plaintext.toString()
    }
}