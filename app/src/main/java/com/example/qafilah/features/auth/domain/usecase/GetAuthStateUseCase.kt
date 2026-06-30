package com.example.qafilah.features.auth.domain.usecase


import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class GetAuthStateUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): AppUser? = repository.getCurrentUser()
}