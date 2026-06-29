package com.example.qafilah.features.auth.domain.repository

import com.example.qafilah.features.auth.domain.model.AppUser

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<AppUser>
    suspend fun signIn(email: String, password: String): Result<AppUser>
    fun getCurrentUser(): AppUser?
    fun signOut()
}