package com.example.qafilah.features.profile.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.repository.ProfileRepository

class GetPersonalDetailsUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(accessToken: String): Result<AppUser> {
        return repository.getPersonalDetails(accessToken)
    }
}
