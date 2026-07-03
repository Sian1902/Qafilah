package com.example.qafilah.features.catalog.data.repo

import com.example.qafilah.features.catalog.data.datasource.AdsCouponDataStore
import com.example.qafilah.features.catalog.domain.repo.AdsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdsRepositoryImpl(
    private val localDataStore: AdsCouponDataStore
) : AdsRepository {

    override suspend fun savePromoCode(code: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                localDataStore.saveCoupon(code)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getPendingPromoCode(): Result<String?> {
        return withContext(Dispatchers.IO) {
            try {
                val code = localDataStore.getSavedCoupon()
                Result.success(code)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun clearPendingPromoCode(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                localDataStore.clearSavedCoupon()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}