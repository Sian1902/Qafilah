package com.example.qafilah.features.assistant.data.remote

import com.google.gson.annotations.SerializedName

data class N8nWebhookResponse(
    @SerializedName("status") val status: String,
    @SerializedName("intent") val intent: String,
    @SerializedName("data") val data: N8nResponseData
)

data class N8nResponseData(
    @SerializedName("message") val message: String
)