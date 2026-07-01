package com.example.qafilah.features.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.qafilah.features.catalog.domain.model.ProductVariant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetSingleProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var localFavoriteState = false
    private var lastLoadedId: String? = null

    fun loadProduct(productId: String) {
        if (productId == lastLoadedId && _uiState.value is ProductDetailUiState.Success) return

        lastLoadedId = productId
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            getProductDetailUseCase(productId)
                .onSuccess { product ->
                    val initialVariant = product.variants.firstOrNull()
                    val initialOptions = initialVariant?.options ?: emptyMap()

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
                        isFavorite = localFavoriteState
                    )
                }
                .onFailure { error ->
                    _uiState.value = ProductDetailUiState.Error(
                        error.message ?: "Failed to load product details"
                    )
                }
        }
    }

    fun selectOption(name: String, value: String) {
        val currentState = _uiState.value as? ProductDetailUiState.Success ?: return
        val updatedOptions = currentState.selectedOptions.toMutableMap().apply {
            put(name, value)
        }
        val matchingVariant = currentState.product.variants.find { variant ->
            variant.options.all { (optName, optValue) -> updatedOptions[optName] == optValue }
        } ?: currentState.selectedVariant

        _uiState.value = currentState.copy(
            selectedVariant = matchingVariant,
            selectedOptions = updatedOptions
        )
    }

    fun toggleFavorite() {
        val currentState = _uiState.value as? ProductDetailUiState.Success ?: return
        localFavoriteState = !currentState.isFavorite
        _uiState.value = currentState.copy(isFavorite = localFavoriteState)
    }
}
