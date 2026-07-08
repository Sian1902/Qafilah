package com.example.qafilah.core.token

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore

interface CryptoManager {
    fun encrypt(unencryptedText: String): String
    fun decrypt(encryptedText: String): String
}

class TinkCryptoManager(private val context: Context) : CryptoManager {

    private companion object {
        const val KEYSET_NAME = "tink_keyset"
        const val PREFERENCE_FILE = "secure_prefs"
        const val MASTER_KEY_URI = "android-keystore://tink_master_key"
    }

    private var aead: Aead

    init {
        AeadConfig.register()

        aead = initKeysetManager()
    }

    private fun initKeysetManager(): Aead {
        return try {
            buildKeysetManager(context)
        } catch (e: Exception) {
            if (e is GeneralSecurityException || e is IOException) {

                try {
                    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                    keyStore.deleteEntry("tink_master_key")
                } catch (keyStoreException: Exception) {
                    keyStoreException.printStackTrace()
                }

                context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit()

                buildKeysetManager(context)
            } else {
                throw e
            }
        }
    }

    private fun buildKeysetManager(context: Context): Aead {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    override fun encrypt(unencryptedText: String): String {
        val cipherText = aead.encrypt(unencryptedText.toByteArray(), null)
        return Base64.encodeToString(cipherText, Base64.DEFAULT)
    }

    override fun decrypt(encryptedText: String): String {
        val plainText = aead.decrypt(Base64.decode(encryptedText, Base64.DEFAULT), null)
        return String(plainText)
    }
}