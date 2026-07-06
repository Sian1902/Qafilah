package com.example.qafilah.features.auth.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository
    ) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}
