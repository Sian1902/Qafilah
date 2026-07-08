package com.example.qafilah.features.assistant.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import com.example.qafilah.features.assistant.domain.usecase.ClearChatHistoryUseCase
import com.example.qafilah.features.assistant.domain.usecase.GetChatHistoryUseCase
import com.example.qafilah.features.assistant.domain.usecase.SendPromptUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.ui_kit.components.home.ProductUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendPromptUseCase: SendPromptUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase,
    private val getSingleProductUseCase: GetSingleProductUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
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
                .collect { domainMessages ->
                    val uiMessages = domainMessages
                        .filter { it.role != "system" }
                        .map { domain ->
                            AssistantMessageUi(
                                role = domain.role,
                                content = domain.content,
                                timestamp = domain.timestamp,
                                products = emptyList()
                            )
                        }
                    _state.update { it.copy(messages = uiMessages) }
                }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userMessage = AssistantMessageUi(role = "user", content = text)
            _state.update { state ->
                state.copy(messages = state.messages + userMessage)
            }

            val result = sendPromptUseCase(text)

            result.onFailure { error ->
                _state.update { state ->
                    state.copy(isLoading = false, error = error.message ?: "Unknown error")
                }
            }

            result.onSuccess { response ->
                val assistantMessage = AssistantMessageUi(
                    role = "assistant",
                    content = response.message,
                    products = emptyList()
                )

                _state.update { state ->
                    state.copy(
                        messages = state.messages + assistantMessage,
                        isLoading = false
                    )
                }

                if (response.productIds.isNotEmpty()) {
                    fetchProductDetailsAndUpdate(response.productIds)
                }
            }
        }
    }

    private suspend fun fetchProductDetailsAndUpdate(productIds: List<String>) {
        try {
            val productDetailsList = coroutineScope {
                productIds.map { id ->
                    async { getSingleProductUseCase(id).getOrNull() }
                }.map { it.await() }
                    .filterNotNull()
            }

            if (productDetailsList.isEmpty()) return

            val productUiModels = productDetailsList.map { product ->
                ProductUiModel(
                    id = product.id,
                    imageUrl = product.images.firstOrNull() ?: "",
                    category = product.tags.firstOrNull()?.uppercase() ?: "",
                    name = product.title,
                    price = convertPriceUseCase(
                        product.variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0
                    ),
                    isFavorite = false
                )
            }

            _state.update { state ->
                val messages = state.messages.toMutableList()
                val lastIndex = messages.indexOfLast { it.role == "assistant" && it.products.isEmpty() }
                if (lastIndex != -1) {
                    messages[lastIndex] = messages[lastIndex].copy(products = productUiModels)
                }
                state.copy(messages = messages)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearChat() {
        viewModelScope.launch {
            clearChatHistoryUseCase()
            _state.update { it.copy(messages = emptyList()) }
        }
    }
}