package com.example.qafilah.features.assistant.data

import com.example.qafilah.features.assistant.data.local.AssistantDao
import com.example.qafilah.features.assistant.data.local.toDomain
import com.example.qafilah.features.assistant.data.local.toEntity
import com.example.qafilah.features.assistant.domain.model.AssistantResponseDomain
import com.example.qafilah.features.assistant.domain.model.ChatMessage
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MockAssistantRepositoryImpl(
    private val dao: AssistantDao
) : AssistantRepository {

    private val contextLimit = 6

    override suspend fun sendMessageAndGetReply(userId: String, userText: String): Result<ChatMessage> {
        return try {
            // 1. Save user message to Room
            val userMessage = ChatMessage(userId = userId, role = "user", content = userText)
            dao.insertMessage(userMessage.toEntity())

            // 2. Fetch sliding window context (To test that your DB query works)
            // We just fetch it to ensure it doesn't crash; the mock doesn't actually read it.
            val recentEntities = dao.getRecentMessages(userId, contextLimit)

            // 3. Simulate network latency (1.5 seconds) so the UI shows the loading spinner
            delay(1500)

            // 4. Generate a fake AI response based on user input
            val mockReplyContent = generateMockReply(userText, recentEntities.size)

            // 5. Save the mock AI reply to Room
            val aiMessage = ChatMessage(userId = userId, role = "assistant", content = mockReplyContent)
            dao.insertMessage(aiMessage.toEntity())

            Result.success(aiMessage)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getChatHistory(userId: String): Flow<List<ChatMessage>> {
        return dao.getAllMessages(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun clearHistory(userId: String) {
        dao.clearHistory(userId)
    }

    override suspend fun sendPrompt(message: String): Result<AssistantResponseDomain> {
        delay(1000)
        return Result.success(
            AssistantResponseDomain(
                message = generateMockReply(message, 0),
                productIds = emptyList()
            )
        )
    }

    // A simple helper to make the mock responses feel slightly interactive
    private fun generateMockReply(userText: String, contextSize: Int): String {
        val lowerText = userText.lowercase()
        return when {
            lowerText.contains("hello") || lowerText.contains("hi") ->
                "Hello! I am the Qafilah mock assistant. How can I help you test today?"
            lowerText.contains("price") || lowerText.contains("cost") ->
                "As a mock AI, everything is free! But usually, I'd check the product database."
            lowerText.contains("memory") ->
                "I can see we have $contextSize messages in our recent context window!"
            else ->
                "This is a mock response to: \"$userText\". Your UI and Room DB are working perfectly."
        }
    }
}