package com.example.qafilah.features.assistant.domain.usecase

import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class ClearChatHistoryUseCase(
    private val repository: AssistantRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val userId = authRepository.getCurrentUser()?.id
            ?: return Result.failure(Exception("User is not authenticated"))

        return try {
            repository.clearHistory(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}