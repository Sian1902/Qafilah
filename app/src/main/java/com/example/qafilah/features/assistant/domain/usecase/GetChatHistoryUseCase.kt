package com.example.qafilah.features.assistant.domain.usecase

import com.example.qafilah.features.assistant.domain.model.ChatMessage
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class GetChatHistoryUseCase(
    private val repository: AssistantRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<ChatMessage>> {
        val userId = authRepository.getCurrentUser()?.id ?: return emptyFlow()
        return repository.getChatHistory(userId)
    }
}