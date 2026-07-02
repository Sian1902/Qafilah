package com.example.qafilah.core.token

interface TokenProvider {
    suspend fun getToken(): String?
}
