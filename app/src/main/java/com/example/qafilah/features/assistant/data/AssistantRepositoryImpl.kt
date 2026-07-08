package com.example.qafilah.features.assistant.data

import com.example.qafilah.core.token.TokenLocalDataSource
import com.example.qafilah.features.assistant.data.remote.AssistantRemoteDataSource
import com.example.qafilah.features.assistant.domain.model.AssistantResponseDomain
import com.example.qafilah.features.assistant.domain.model.ChatMessage
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AssistantRepositoryImpl(
    private val remoteDataSource: AssistantRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : AssistantRepository {

    override fun getChatHistory(userId: String): Flow<List<ChatMessage>> {
        return flowOf(emptyList())
    }

    override suspend fun clearHistory(userId: String) {
        // no-op
    }

    override suspend fun sendPrompt(message: String): Result<AssistantResponseDomain> {
        val userId = tokenLocalDataSource.getToken()
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            val response = remoteDataSource.sendPrompt(userId, message)
            Result.success(
                AssistantResponseDomain(
                    message = response.message,
                    productIds = response.products
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}