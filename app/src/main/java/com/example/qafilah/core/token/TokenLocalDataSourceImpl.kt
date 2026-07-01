package com.example.qafilah.core.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class TokenLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager
) : TokenLocalDataSource {

    companion object {
        val KEY_SHOPIFY_TOKEN = stringPreferencesKey("shopify_customer_access_token")
    }

    override suspend fun saveToken(token: String) {
        val encryptedToken = cryptoManager.encrypt(token)
        dataStore.edit { preferences ->
            preferences[KEY_SHOPIFY_TOKEN] = encryptedToken
        }
    }

    override suspend fun getToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[KEY_SHOPIFY_TOKEN]?.let { encryptedString ->
            try {
                cryptoManager.decrypt(encryptedString)
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_SHOPIFY_TOKEN)
        }
    }
}