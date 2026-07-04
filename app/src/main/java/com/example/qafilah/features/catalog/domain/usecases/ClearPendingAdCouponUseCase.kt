package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.repo.AdsRepository

class ClearPendingAdCouponUseCase(
    private val repository: AdsRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearPendingPromoCode()
    }
}