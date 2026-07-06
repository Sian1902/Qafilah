package com.example.qafilah.features.assistant.data.remote

import com.google.gson.annotations.SerializedName

data class ChatRequestDto(
    @SerializedName("model")
    val model: String = "llama-3.3-70b-versatile",
    @SerializedName("messages")
    val messages: List<MessageDto>,
    @SerializedName("temperature")
    val temperature: Double = 0.7
)

data class MessageDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)