package com.example.qafilah.auth.domain.usecase

import com.example.qafilah.auth.domain.model.AppUser
import com.example.qafilah.auth.domain.repository.AuthRepository

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser> {
        // You can add validation constraints here later if needed (e.g., password length checks)
        return repository.signUp(email, password)
    }
}