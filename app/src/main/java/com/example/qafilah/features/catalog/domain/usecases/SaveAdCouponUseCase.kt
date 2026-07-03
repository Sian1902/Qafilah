package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.repo.AdsRepository

class SaveAdCouponUseCase(
    private val repository: AdsRepository
) {
    suspend operator fun invoke(code: String): Result<Unit> {
        return repository.savePromoCode(code.uppercase())
    }
}