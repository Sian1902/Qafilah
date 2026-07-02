package com.example.qafilah.features.cart.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class CartLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : CartLocalDataSource {

    companion object {
        private val KEY_CART_ID = stringPreferencesKey("shopify_cart_id")
    }

    override suspend fun saveCartId(cartId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_CART_ID] = cartId
        }
    }

    override suspend fun getCartId(): String? {
        val preferences = dataStore.data.first()
        return preferences[KEY_CART_ID]
    }

    override suspend fun deleteCartId() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_CART_ID)
        }
    }
}