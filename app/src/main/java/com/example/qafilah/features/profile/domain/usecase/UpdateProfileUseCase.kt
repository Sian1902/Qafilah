package com.example.qafilah.features.profile.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.repository.ProfileRepository
import com.example.qafilah.features.profile.domain.repository.UpdateProfileRepositoryParams

sealed class UpdateProfileError : Throwable() {
    class FirstNameEmpty : UpdateProfileError()
    class InvalidEmail : UpdateProfileError()
    class PhoneEmpty : UpdateProfileError()
    class InvalidPhoneFormat : UpdateProfileError()
}

data class UpdateProfileParams(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val originalEmail: String?
)

class UpdateProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(params: UpdateProfileParams): Result<Pair<AppUser, Boolean>> {
        if (params.firstName.isBlank()) {
            return Result.failure(UpdateProfileError.FirstNameEmpty())
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(params.email).matches()) {
            return Result.failure(UpdateProfileError.InvalidEmail())
        }
        if (params.phone.isBlank()) {
            return Result.failure(UpdateProfileError.PhoneEmpty())
        }
        if (!params.phone.startsWith("+")) {
            return Result.failure(UpdateProfileError.InvalidPhoneFormat())
        }

        return repository.updateProfile(
            UpdateProfileRepositoryParams(
                firstName = params.firstName,
                lastName = params.lastName,
                email = params.email,
                phone = params.phone
            )
        ).map { updatedUser ->
            val emailChanged = params.originalEmail != null && params.email != params.originalEmail
            Pair(updatedUser, emailChanged)
        }
    }
}
