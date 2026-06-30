package com.example.qafilah.core.token

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

interface CryptoManager {
    fun encrypt(unencryptedText: String): String
    fun decrypt(encryptedText: String): String
}

class TinkCryptoManager(context: Context) : CryptoManager {

    init {
        AeadConfig.register()
    }

    private val aead: Aead = AndroidKeysetManager.Builder()
        .withSharedPref(context, "tink_keyset", "secure_prefs")
        .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
        .withMasterKeyUri("android-keystore://tink_master_key")
        .build()
        .keysetHandle
        .getPrimitive(Aead::class.java)

    override fun encrypt(unencryptedText: String): String {
        val cipherText = aead.encrypt(unencryptedText.toByteArray(), null)
        return Base64.encodeToString(cipherText, Base64.DEFAULT)
    }

    override fun decrypt(encryptedText: String): String {
        val plainText = aead.decrypt(Base64.decode(encryptedText, Base64.DEFAULT), null)
        return String(plainText)
    }
}