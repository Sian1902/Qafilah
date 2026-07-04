package com.example.qafilah.features.catalog.data.datasource

interface AdsCouponDataStore {
    suspend fun saveCoupon(code: String)
    suspend fun getSavedCoupon(): String?
    suspend fun clearSavedCoupon()
}