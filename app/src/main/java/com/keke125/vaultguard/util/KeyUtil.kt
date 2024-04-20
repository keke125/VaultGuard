package com.keke125.vaultguard.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey


class KeyUtil {
    private val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    private val alias = "AESKey"
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

    private fun generateKey(): KeyPair {
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            setKeySize(521)
            build()
        }

        kpg.initialize(parameterSpec)

        return kpg.generateKeyPair()
    }
}