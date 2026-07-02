package com.example.qafilah.features.profile.domain.usecase

import com.example.qafilah.features.profile.domain.model.CustomerProfile
import com.example.qafilah.features.profile.domain.repository.ProfileRepository

class GetCustomerProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(accessToken: String): Result<CustomerProfile> {
        return repository.getCustomerProfile(accessToken)
    }
}