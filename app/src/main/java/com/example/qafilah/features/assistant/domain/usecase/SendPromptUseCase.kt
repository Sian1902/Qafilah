package com.example.qafilah.features.assistant.domain.usecase

import com.example.qafilah.features.assistant.domain.model.AssistantResponseDomain
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SendPromptUseCase(
    private val repository: AssistantRepository,
) {
    suspend operator fun invoke(message: String): Result<AssistantResponseDomain> {
        return repository.sendPrompt( message)
    }
}