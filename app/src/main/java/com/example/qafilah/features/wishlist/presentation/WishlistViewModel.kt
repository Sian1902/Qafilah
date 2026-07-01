package com.example.qafilah.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import com.example.qafilah.features.wishlist.domain.usecase.GetWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import com.example.ui_kit.components.home.ProductUiModel
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
    data class RemoveFromWishlist(val productId: String) : WishlistIntent

    // FIX: Cleanly moved inside WishlistIntent architectural scope
    data class PromptRemove(val product: ProductUiModel) : WishlistIntent
    object ConfirmRemoval : WishlistIntent
    object DismissRemoval : WishlistIntent
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
    val items: List<ProductUiModel> = emptyList(),
    val productToConfirmRemove: ProductUiModel? = null
)

class WishlistViewModel(
    private val requireAuth: RequireAuth,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase
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
            is WishlistIntent.RemoveFromWishlist -> removeItemFromDb(intent.productId)

            // FIX: Handled as valid WishlistIntent cases
            is WishlistIntent.PromptRemove -> {
                _state.update { it.copy(productToConfirmRemove = intent.product) }
            }
            WishlistIntent.ConfirmRemoval -> {
                state.value.productToConfirmRemove?.let { product ->
                    removeItemFromDb(product.id)
                }
                _state.update { it.copy(productToConfirmRemove = null) }
            }
            WishlistIntent.DismissRemoval -> {
                _state.update { it.copy(productToConfirmRemove = null) }
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
                getWishlistUseCase().collect { actualDomainItems ->
                    val uiModels = actualDomainItems.map { it.toUiModel() }
                    _state.update {
                        it.copy(isLoading = false, items = uiModels)
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load wishlist")
                }
            }
        }
    }

    private fun removeItemFromDb(productId: String) {
        _state.update { state ->
            state.copy(
                items = state.items.map { item ->
                    if (item.id == productId) item.copy(isFavorite = false) else item
                }
            )
        }

        viewModelScope.launch {
            try {
                removeFromWishlistUseCase(productId)
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        items = state.items.map { item ->
                            if (item.id == productId) item.copy(isFavorite = true) else item
                        },
                        errorMessage = e.message ?: "Failed to remove item"
                    )
                }
            }
        }
    }

    private fun WishlistItem.toUiModel(): ProductUiModel {
        return ProductUiModel(
            id = this.productId,
            imageUrl = this.remoteImageUrl ?: this.localImagePath ?: "",
            category = this.vendor ?: "Unknown Category",
            name = this.title,
            price = "${this.currencyCode} ${this.price}",
            isFavorite = true
        )
    }
}