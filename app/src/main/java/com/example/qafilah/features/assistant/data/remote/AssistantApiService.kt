package com.example.qafilah.features.assistant.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AssistantApiService {
    @GET("webhook/dda1f938-157b-4993-9842-cc59971237ec")
    suspend fun sendPrompt(
        @Query("customerAccessToken") customerAccessToken: String,
        @Query("message") message: String
    ): Response<AssistantResponse>
}