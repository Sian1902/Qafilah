package com.example.qafilah.features.assistant.data.remote
import retrofit2.http.GET
import retrofit2.http.Query

interface AssistantApiService {
    @GET("webhook-test/dda1f938-157b-4993-9842-cc59971237ec")
    suspend fun fetchAssistantResponse(
        @Query("message") message: String
    ): N8nWebhookResponse
}