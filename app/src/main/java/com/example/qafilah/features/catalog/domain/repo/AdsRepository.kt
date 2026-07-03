package com.example.qafilah.features.catalog.domain.repo

interface AdsRepository {
    suspend fun savePromoCode(code: String): Result<Unit>
    suspend fun getPendingPromoCode(): Result<String?>
    suspend fun clearPendingPromoCode(): Result<Unit>
}