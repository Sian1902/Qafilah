package com.example.qafilah.features.profile.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.repository.ProfileRepository

sealed class UpdateProfileError : Throwable() {
    object FirstNameEmpty : UpdateProfileError()
    object InvalidEmail : UpdateProfileError()
    object PhoneEmpty : UpdateProfileError()
    object InvalidPhoneFormat : UpdateProfileError()
}

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
            return Result.failure(UpdateProfileError.FirstNameEmpty)
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(UpdateProfileError.InvalidEmail)
        }
        if (phone.isBlank()) {
            return Result.failure(UpdateProfileError.PhoneEmpty)
        }
        if (!phone.startsWith("+")) {
            return Result.failure(UpdateProfileError.InvalidPhoneFormat)
        }

        return repository.updateProfile(accessToken, firstName, lastName, email, phone)
            .map { updatedUser ->
                val emailChanged = originalEmail != null && email != originalEmail
                Pair(updatedUser, emailChanged)
            }
    }
}
