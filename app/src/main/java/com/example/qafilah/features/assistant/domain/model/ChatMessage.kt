package com.example.qafilah.features.assistant.domain.model

data class ChatMessage(
    val id: Int = 0,
    val userId: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)