package com.example.qafilah.features.auth.data.datasource

import com.example.qafilah.features.auth.domain.model.AppUser

interface FirebaseAuthRemoteDataSource {
    suspend fun signUpPrimary(name: String, email: String, password: String): AppUser
    suspend fun signInPrimary(email: String, password: String): AppUser
    fun getCurrentUser(): AppUser?
    fun signOut()
    suspend fun signInWithGoogle(idToken: String): AppUser
}