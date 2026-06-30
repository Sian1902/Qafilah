package com.example.qafilah.features.auth.domain.repository

import com.example.qafilah.features.auth.domain.model.AppUser

interface AuthRepository {


    suspend fun authenticatePrimary(email: String, password: String): Result<AppUser>

    suspend fun registerPrimary(email: String, password: String, fullName: String): Result<AppUser>


    suspend fun authenticateStorefront(email: String, password: String): Result<String>

    suspend fun registerStorefront(
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null
    ): Result<Unit>


    suspend fun saveSessionToken(token: String)

    fun getCurrentUser(): AppUser?

    suspend fun signOut()
}