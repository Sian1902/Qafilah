package com.example.qafilah.features.assistant.data.remote

class AssistantRemoteDataSource(
    private val apiService: AssistantApiService
) {
    suspend fun fetchAssistantResponse(userMessage: String): N8nWebhookResponse {
        return apiService.fetchAssistantResponse(userMessage)
    }
}