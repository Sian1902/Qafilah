package com.example.qafilah.features.assistant.domain.usecase

import com.example.qafilah.features.assistant.domain.model.ChatMessage
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SendPromptUseCase(
    private val repository: AssistantRepository, private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userText: String): Result<ChatMessage> {
        val userId = authRepository.getCurrentUser()?.id
            ?: return Result.failure(Exception("User is not authenticated"))

        return repository.sendMessageAndGetReply(userId, userText)
    }
}