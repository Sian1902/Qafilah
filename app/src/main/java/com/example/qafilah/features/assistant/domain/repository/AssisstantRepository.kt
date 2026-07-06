package com.example.qafilah.features.assistant.domain.repository

import com.example.qafilah.features.assistant.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AssistantRepository {
    suspend fun sendMessageAndGetReply(userId: String, userText: String): Result<ChatMessage>
    fun getChatHistory(userId: String): Flow<List<ChatMessage>>
    suspend fun clearHistory(userId: String)
}