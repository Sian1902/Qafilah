package com.example.qafilah.features.profile.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        accessToken: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        originalEmail: String?
    ): Result<Pair<AppUser, Boolean>> {
        if (firstName.isBlank()) {
            return Result.failure(Exception("First name cannot be blank"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        if (phone.isBlank()) {
            return Result.failure(Exception("Phone number cannot be blank"))
        }
        if (!phone.startsWith("+")) {
            return Result.failure(Exception("Include '+' and country code (e.g. +2010...)"))
        }

        return repository.updateProfile(accessToken, firstName, lastName, email, phone)
            .map { updatedUser ->
                val emailChanged = originalEmail != null && email != originalEmail
                Pair(updatedUser, emailChanged)
            }
    }
}
