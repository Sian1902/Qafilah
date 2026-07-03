package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.repo.AdsRepository

class GetPendingAdCouponUseCase(
    private val repository: AdsRepository
) {
    suspend operator fun invoke(): Result<String?> {
        return repository.getPendingPromoCode()
    }
}