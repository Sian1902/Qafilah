package com.example.qafilah.features.cart.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.cart.domain.usecase.GetCartUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CartIntent {
    object EnterScreen : CartIntent
    object NavigateToLogin : CartIntent
    object NavigateToSignUp : CartIntent
    object DismissLoginPrompt : CartIntent
}

sealed interface CartEvent {
    object NavigateToLogin : CartEvent
    object NavigateToSignUp : CartEvent
    object NavigateToHome : CartEvent
}

data class CartState(
    val isLoading: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val errorMessage: String? = null
)

class CartViewModel(
    private val requireAuth: RequireAuth,
    private val getCartUseCase: GetCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _events = Channel<CartEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val cart = getCartUseCase()?.let {
                Log.d("CartViewModel", "Cart: $it, ${it.lines.first().merchandise.product.title}")
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            CartIntent.EnterScreen -> checkAuthAndLoad()
            CartIntent.NavigateToLogin -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToLogin)
            }

            CartIntent.NavigateToSignUp -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToSignUp)
            }

            CartIntent.DismissLoginPrompt -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToHome)
            }
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
            _state.update { it.copy(isLoading = true) }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

