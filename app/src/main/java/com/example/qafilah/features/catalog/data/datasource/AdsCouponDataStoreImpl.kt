package com.example.qafilah.features.catalog.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class AdsCouponDataStoreImpl(
    private val dataStore: DataStore<Preferences>
) : AdsCouponDataStore {

    private companion object {
        val SAVED_COUPON_KEY = stringPreferencesKey("saved_ad_coupon")
    }

    override suspend fun saveCoupon(code: String) {
        dataStore.edit { preferences ->
            preferences[SAVED_COUPON_KEY] = code
        }
    }

    override suspend fun getSavedCoupon(): String? {
        val preferences = dataStore.data.first()
        return preferences[SAVED_COUPON_KEY]
    }

    override suspend fun clearSavedCoupon() {
        dataStore.edit { preferences ->
            preferences.remove(SAVED_COUPON_KEY)
        }
    }
}