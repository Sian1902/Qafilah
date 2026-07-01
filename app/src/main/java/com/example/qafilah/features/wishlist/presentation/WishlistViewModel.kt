package com.example.qafilah.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.wishlist.domain.model.WishlistItem //
import com.example.ui_kit.components.home.ProductUiModel //
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
    val errorMessage: String? = null,
    val items: List<ProductUiModel> = emptyList()
)

class WishlistViewModel(
    private val requireAuth: RequireAuth,
    // TODO: Inject your actual data layer here (e.g., WishlistRepository or GetWishlistUseCase)
    // private val repository: WishlistRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow() //

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
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // TODO: Replace with your actual repository call
                // val actualDomainItems: List<WishlistItem> = repository.getWishlistItems()
                val actualDomainItems: List<WishlistItem> = emptyList()

                // Map the domain models[cite: 16] to UI models[cite: 17]
                val uiModels = actualDomainItems.map { it.toUiModel() }

                _state.update {
                    it.copy(
                        isLoading = false,
                        items = uiModels
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load wishlist"
                    )
                }
            }
        }
    }

    /**
     * Extension function to map your actual WishlistItem domain model
     * to the ProductUiModel required by the ProductCard[cite: 17].
     */
    private fun WishlistItem.toUiModel(): ProductUiModel {
        return ProductUiModel(
            id = this.productId, //[cite: 16, 17]
            imageUrl = this.remoteImageUrl ?: this.localImagePath ?: "", // Fallback if both are null[cite: 16, 17]
            category = this.vendor ?: "Unknown Category", //[cite: 16, 17]
            name = this.title, //[cite: 16, 17]
            price = "${this.currencyCode} ${this.price}", // Formatting price with currency[cite: 16, 17]
            isFavorite = true // It is in the wishlist, so it is a favorite[cite: 17]
        )
    }
}