package com.example.qafilah.core.token

interface TokenLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun clearToken()
    suspend fun getToken(): String?
}