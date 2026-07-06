package com.example.qafilah.features.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.cart.domain.usecase.AddCartItemUseCase
import com.example.qafilah.features.catalog.domain.model.ProductVariant
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
sealed interface ProductDetailEvent {
    data class ShowToast(val message: String) : ProductDetailEvent
}

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetSingleProductUseCase,
    private val isProductWishlistedUseCase: IsProductWishlistedUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _events = Channel<ProductDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var lastLoadedId: String? = null

    fun loadProduct(productId: String, fallbackErrorMessage: String) {
        if (productId == lastLoadedId && _uiState.value is ProductDetailUiState.Success) return

        lastLoadedId = productId
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            getProductDetailUseCase(productId)
                .onSuccess { product ->
                    val initialVariant = product.variants.firstOrNull()
                    val initialOptions = initialVariant?.options ?: emptyMap()

                    val convertedPrice =
                        convertPriceUseCase(initialVariant?.price?.toDoubleOrNull() ?: 0.0)

                    _uiState.value = ProductDetailUiState.Success(
                        product = product,
                        selectedVariant = initialVariant ?: ProductVariant(
                            id = "",
                            title = "Default",
                            price = "0.00",
                            compareAtPrice = null,
                            inventoryQuantity = null,
                            options = emptyMap()
                        ),
                        selectedOptions = initialOptions,
                        isFavorite = false,
                        displayPrice = convertedPrice
                    )

                    observeWishlistState(productId)
                }
                .onFailure { error ->
                    _uiState.value = ProductDetailUiState.Error(
                        error.message ?: fallbackErrorMessage
                    )
                }
        }
    }

    private fun observeWishlistState(productId: String) {
        viewModelScope.launch {
            isProductWishlistedUseCase(productId).collect { isWishlisted ->
                val current = _uiState.value as? ProductDetailUiState.Success ?: return@collect
                _uiState.value = current.copy(isFavorite = isWishlisted)
            }
        }
    }

    fun selectOption(name: String, value: String) {
        val current = _uiState.value as? ProductDetailUiState.Success ?: return
        val updatedOptions = current.selectedOptions.toMutableMap().apply { put(name, value) }
        val matchingVariant = current.product.variants.find { variant ->
            variant.options.all { (optName, optValue) -> updatedOptions[optName] == optValue }
        } ?: current.selectedVariant

        viewModelScope.launch {
            val convertedPrice = convertPriceUseCase(matchingVariant.price.toDoubleOrNull() ?: 0.0)
            _uiState.value = current.copy(
                selectedVariant = matchingVariant,
                selectedOptions = updatedOptions,
                displayPrice = convertedPrice
            )
        }
    }

    fun toggleFavorite(notLoggedInMessage: String, fallbackErrorMessage: String, addedToWishlistTemplate: String) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null) {
            _events.trySend(ProductDetailEvent.ShowToast(notLoggedInMessage))
            return
        }

        val current = _uiState.value as? ProductDetailUiState.Success ?: return
        val product = current.product
        val wasFavorite = current.isFavorite

        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    removeFromWishlistUseCase(product.id)
                } else {
                    addToWishlistUseCase(
                        productId = product.id,
                        handle = product.id,
                        title = product.title,
                        imageUrl = product.images.firstOrNull(),
                        vendor = product.vendor,
                        price = product.variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0,
                        currencyCode = "USD"
                    )
                    _events.trySend(ProductDetailEvent.ShowToast(String.format(addedToWishlistTemplate, product.title)))
                }
            } catch (e: Exception) {
                _events.trySend(ProductDetailEvent.ShowToast(e.message ?: fallbackErrorMessage))
            }
        }
    }

    fun addToCart(variantId: String, quantity: Int = 1, fallbackErrorMessage: String, notLoggedInMessage: String, successMessage: String) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null) {
            _events.trySend(ProductDetailEvent.ShowToast(notLoggedInMessage))
            return
        }

        val currentState = _uiState.value as? ProductDetailUiState.Success ?: return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isAddingToCart = true, addToCartError = null)

            try {
                addCartItemUseCase(variantId, quantity)
                _uiState.value = currentState.copy(isAddingToCart = false)
                _events.trySend(ProductDetailEvent.ShowToast(successMessage))
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isAddingToCart = false,
                    addToCartError = e.message ?: fallbackErrorMessage
                )
            }
        }
    }

    fun dismissCartError() {
        val currentState = _uiState.value as? ProductDetailUiState.Success ?: return
        _uiState.value = currentState.copy(addToCartError = null)
    }

}
