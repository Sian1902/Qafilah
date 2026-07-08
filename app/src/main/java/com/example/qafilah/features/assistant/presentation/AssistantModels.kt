package com.example.qafilah.features.assistant.presentation

import com.example.ui_kit.components.home.ProductUiModel

data class AssistantMessageUi(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val products: List<ProductUiModel> = emptyList()
)

data class AssistantState(
    val messages: List<AssistantMessageUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)