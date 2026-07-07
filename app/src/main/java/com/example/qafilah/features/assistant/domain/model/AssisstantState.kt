package com.example.qafilah.features.assistant.domain.model

data class AssistantState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)