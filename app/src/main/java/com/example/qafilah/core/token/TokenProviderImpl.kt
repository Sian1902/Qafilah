package com.example.qafilah.core.token

class TokenProviderImpl(
    private val tokenLocalDataSource: TokenLocalDataSource
) : TokenProvider {
    override suspend fun getToken(): String? {
        return tokenLocalDataSource.getToken()
    }
}
