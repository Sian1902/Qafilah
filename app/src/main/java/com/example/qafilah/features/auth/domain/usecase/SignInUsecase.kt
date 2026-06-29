package com.example.qafilah.features.auth.domain.usecase

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email or password cannot be empty"))
        }
        return repository.signIn(email, password)
    }
}