package com.example.qafilah.features.assistant.domain.model

data class AssistantResponseDomain(
    val message: String,
    val productIds: List<String> = emptyList()
)