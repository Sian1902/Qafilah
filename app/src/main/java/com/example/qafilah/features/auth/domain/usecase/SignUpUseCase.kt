package com.example.qafilah.features.auth.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String
    ): Result<AppUser> {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            return Result.failure(IllegalArgumentException("Fields cannot be empty"))
        }

        return try {
            val user = repository.registerPrimary(email, password, fullName).getOrThrow()

            repository.registerStorefront(
                email = email,
                password = password,
                firstName = user.firstName,
                lastName = user.lastName
            ).getOrThrow()

            val token = repository.authenticateStorefront(email, password).getOrThrow()
            repository.saveSessionToken(token)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}