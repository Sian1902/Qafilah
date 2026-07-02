package com.example.qafilah.features.cart.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.cart.domain.usecase.FetchCartUseCase
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.qafilah.features.cart.presentation.contract.CartEvent
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.contract.CartUIState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val requireAuth: RequireAuth,
    private val observeCartStateUseCase: ObserveCartStateUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartUIState())
    val state: StateFlow<CartUIState> = _state.asStateFlow()

    private val _events = Channel<CartEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeCartStateUseCase().collect { storeCart ->
                _state.update { it.copy(cart = storeCart) }
            }
        }
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.EnterScreen -> checkAuthAndLoad()
            is CartIntent.NavigateToLogin -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToLogin)
            }
            is CartIntent.NavigateToSignUp -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToSignUp)
            }
            is CartIntent.DismissLoginPrompt -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToHome)
            }

            is CartIntent.IncreaseQuantity -> handleIncreaseQuantity(intent.lineId)
            is CartIntent.DecreaseQuantity -> handleDecreaseQuantity(intent.lineId)
            is CartIntent.RemoveItem -> handleRemoveItem(intent.lineId)
            is CartIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun checkAuthAndLoad() {
        requireAuth.invoke(
            onAuthenticated = { loadCart() },
            onGuest = { _state.update { it.copy(showLoginPrompt = true) } }
        )
    }

    private fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                fetchCartUseCase()
                Log.d("CartViewModel", "Cart loaded successfully: ${state.value.cart}")
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = e.message ?: "Failed to load cart"
                    )
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleIncreaseQuantity(lineId: String) {
        val currentQuantity = currentLineQuantity(lineId) ?: run {
            _state.update { it.copy(errorMessage = "Cart item not found") }
            return
        }

        viewModelScope.launch {
            try {
                updateCartItemQuantityUseCase(lineId, currentQuantity + 1)
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    private fun handleDecreaseQuantity(lineId: String) {
        val currentQuantity = currentLineQuantity(lineId) ?: run {
            _state.update { it.copy(errorMessage = "Cart item not found") }
            return
        }

        if (currentQuantity <= 1) {
            handleRemoveItem(lineId)
            return
        }

        viewModelScope.launch {
            try {
                updateCartItemQuantityUseCase(lineId, currentQuantity - 1)
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    private fun handleRemoveItem(lineId: String) {
        viewModelScope.launch {
            try {
                removeCartItemUseCase(lineId)
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    private fun currentLineQuantity(lineId: String): Int? {
        return _state.value.cart?.lines?.firstOrNull { it.id == lineId }?.quantity
    }
}
