package com.example.qafilah.features.auth.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.model.AuthError
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SignInUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<AppUser> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Credentials cannot be empty"))
        }

        return try {
            val user = repository.authenticatePrimary(email, password).getOrThrow()

            var storefrontTokenResult = repository.authenticateStorefront(email, password)

            if (storefrontTokenResult.exceptionOrNull() is AuthError.StorefrontProfileMissing) {
                repository.registerStorefront(
                    email = email,
                    password = password,
                    firstName = user.firstName ?: "Valued",
                    lastName = user.lastName ?: "Customer"
                ).getOrThrow()

                storefrontTokenResult = repository.authenticateStorefront(email, password)
            }

            val token = storefrontTokenResult.getOrThrow()
            repository.saveSessionToken(token)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}