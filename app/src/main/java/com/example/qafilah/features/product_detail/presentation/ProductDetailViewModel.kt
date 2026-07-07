package com.example.qafilah.features.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.cart.domain.usecase.AddCartItemUseCase
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.ProductVariant
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistParams
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
    private var currentProduct: ProductDetails? = null
    private var selectedVariant: ProductVariant? = null
    private var selectedOptions: Map<String, String> = emptyMap()
    private var isFavorite: Boolean = false
    private var labels: ProductDetailLabels? = null

    fun loadProduct(productId: String, fallbackErrorMessage: String, labels: ProductDetailLabels) {
        this.labels = labels
        if (productId == lastLoadedId && _uiState.value is ProductDetailUiState.Success) return

        lastLoadedId = productId
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            getProductDetailUseCase(productId)
                .onSuccess { product ->
                    currentProduct = product
                    val initialVariant = product.variants.firstOrNull() ?: ProductVariant(
                        id = "",
                        title = "Default",
                        price = "0.00",
                        compareAtPrice = null,
                        inventoryQuantity = null,
                        options = emptyMap()
                    )
                    selectedVariant = initialVariant
                    selectedOptions = initialVariant.options
                    
                    updateUiState()
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
            isProductWishlistedUseCase(productId).collect { wishlisted ->
                isFavorite = wishlisted
                updateUiState()
            }
        }
    }

    private suspend fun updateUiState() {
        val product = currentProduct ?: return
        val variant = selectedVariant ?: return
        val labels = labels ?: return

        val displayPrice = convertPriceUseCase(variant.price.toDoubleOrNull() ?: 0.0)
        
        val optionGroups = mutableMapOf<String, MutableList<String>>()
        for (v in product.variants) {
            for ((name, value) in v.options) {
                if (name.equals("Title", ignoreCase = true) && value.equals("Default Title", ignoreCase = true)) continue
                val list = optionGroups.getOrPut(name) { mutableListOf() }
                if (!list.contains(value)) list.add(value)
            }
        }

        val inStock = variant.inventoryQuantity != null && variant.inventoryQuantity > 0
        val stockText = if (inStock) {
            String.format(labels.inStockTemplate, variant.inventoryQuantity)
        } else {
            labels.outOfStockText
        }

        val uiModel = ProductDetailUiModel(
            id = product.id,
            title = product.title,
            description = product.description ?: "",
            images = product.images,
            tag = product.tags.firstOrNull()?.uppercase() ?: labels.defaultCollectionLabel,
            rating = product.rating,
            reviewCount = product.ratingCount,
            ratingLabel = String.format(labels.ratingLabelTemplate, product.rating ?: 0.0, product.ratingCount ?: 0),
            displayPrice = displayPrice,
            stockText = stockText,
            stockColorInt = if (inStock) 0xFF4CAF50.toInt() else 0xFFF44336.toInt(),
            isInStock = inStock,
            optionGroups = optionGroups.mapValues { it.value.toList() },
            selectedOptions = selectedOptions,
            selectedVariantId = variant.id,
            isFavorite = isFavorite
        )

        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            _uiState.value = currentState.copy(product = uiModel)
        } else {
            _uiState.value = ProductDetailUiState.Success(product = uiModel)
        }
    }

    fun selectOption(name: String, value: String) {
        val product = currentProduct ?: return
        selectedOptions = selectedOptions.toMutableMap().apply { put(name, value) }
        
        selectedVariant = product.variants.find { variant ->
            variant.options.all { (optName, optValue) -> selectedOptions[optName] == optValue }
        } ?: selectedVariant

        viewModelScope.launch {
            updateUiState()
        }
    }

    fun toggleFavorite(notLoggedInMessage: String, fallbackErrorMessage: String, addedToWishlistTemplate: String) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null) {
            _events.trySend(ProductDetailEvent.ShowToast(notLoggedInMessage))
            return
        }

        val product = currentProduct ?: return
        val wasFavorite = isFavorite

        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    removeFromWishlistUseCase(product.id)
                } else {
                    addToWishlistUseCase(
                        AddToWishlistParams(
                            productId = product.id,
                            handle = product.id,
                            title = product.title,
                            imageUrl = product.images.firstOrNull(),
                            vendor = product.vendor,
                            price = product.variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0,
                            currencyCode = "USD"
                        )
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
