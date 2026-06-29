package com.example.qafilah.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WishlistIntent {
    object EnterScreen : WishlistIntent
    object NavigateToLogin : WishlistIntent
    object NavigateToSignUp : WishlistIntent
    object DismissLoginPrompt : WishlistIntent
}

sealed interface WishlistEvent {
    object NavigateToLogin : WishlistEvent
    object NavigateToSignUp : WishlistEvent
    object NavigateToHome : WishlistEvent
}

data class WishlistState(
    val isLoading: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val errorMessage: String? = null
)

class WishlistViewModel(
    private val requireAuth: RequireAuth
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    private val _events = Channel<WishlistEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onIntent(intent: WishlistIntent) {
        when (intent) {
            WishlistIntent.EnterScreen -> checkAuthAndLoad()
            WishlistIntent.NavigateToLogin -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(WishlistEvent.NavigateToLogin)
            }

            WishlistIntent.NavigateToSignUp -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(WishlistEvent.NavigateToSignUp)
            }

            WishlistIntent.DismissLoginPrompt -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(WishlistEvent.NavigateToHome)
            }
        }
    }

    private fun checkAuthAndLoad() {
        requireAuth.invoke(
            onAuthenticated = { loadWishlist() },
            onGuest = { _state.update { it.copy(showLoginPrompt = true) } }
        )
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

