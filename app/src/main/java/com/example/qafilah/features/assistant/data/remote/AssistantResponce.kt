package com.example.qafilah.features.assistant.data.remote

data class AssistantResponse(
    val message: String,
    val products: List<String> = emptyList()
)