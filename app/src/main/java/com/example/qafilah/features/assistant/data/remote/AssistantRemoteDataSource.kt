package com.example.qafilah.features.assistant.data.remote

class AssistantRemoteDataSource(
    private val apiService: AssistantApiService
) {
    suspend fun sendPrompt(token: String, message: String): AssistantResponse {
        val response = apiService.sendPrompt(token, message)
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
        val body = response.body()
        if (body == null) {
            throw Exception("Empty response body")
        }
        return body
    }
}