package com.example.qafilah.auth.domain.repository

import com.example.qafilah.auth.domain.model.AppUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<AppUser>
    fun getCurrentUser(): AppUser?
    fun signOut()
}