package com.example.qafilah.features.assistant.data


import com.example.qafilah.features.assistant.data.local.AssistantDao
import com.example.qafilah.features.assistant.data.local.toDomain
import com.example.qafilah.features.assistant.data.local.toEntity
import com.example.qafilah.features.assistant.data.remote.AssistantRemoteDataSource
import com.example.qafilah.features.assistant.data.remote.ChatRequestDto
import com.example.qafilah.features.assistant.data.remote.MessageDto
import com.example.qafilah.features.assistant.domain.model.ChatMessage
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class AssistantRepositoryImpl(
    private val dao: AssistantDao,
    private val remoteDataSource: AssistantRemoteDataSource
) : AssistantRepository {

    override suspend fun sendMessageAndGetReply(userId: String, userText: String): Result<ChatMessage> {
        return try {
            val userMessage = ChatMessage(userId = userId, role = "user", content = userText)
            dao.insertMessage(userMessage.toEntity())

            val response = remoteDataSource.fetchAssistantResponse(userText)

            val aiReplyContent = response.data.message

            if (response.status != "success" || aiReplyContent.isBlank()) {
                throw Exception("Failed to get a valid response from the assistant")
            }

            val aiMessage = ChatMessage(userId = userId, role = "assistant", content = aiReplyContent)
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
}