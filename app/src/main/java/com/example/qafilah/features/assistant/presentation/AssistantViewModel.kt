package com.example.qafilah.features.assistant.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.assistant.domain.model.AssistantState
import com.example.qafilah.features.assistant.domain.usecase.ClearChatHistoryUseCase
import com.example.qafilah.features.assistant.domain.usecase.GetChatHistoryUseCase
import com.example.qafilah.features.assistant.domain.usecase.SendPromptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendPromptUseCase: SendPromptUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AssistantState())
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {

            getChatHistoryUseCase()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { messages ->
                    val displayMessages = messages.filter { it.role != "system" }
                    _state.update { it.copy(messages = displayMessages) }
                }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }


            val result = sendPromptUseCase(text)

            result.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
            result.onSuccess {
                
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            clearChatHistoryUseCase()
        }
    }
}