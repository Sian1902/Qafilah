package com.example.qafilah.features.product_detail.presentation

import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.ProductVariant

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState

    data class Success(
        val product: ProductDetails,
        val selectedVariant: ProductVariant,
        val selectedOptions: Map<String, String>,
        val isFavorite: Boolean,
        val isAddingToCart: Boolean = false,
        val addToCartError: String? = null
    ) : ProductDetailUiState

    data class Error(val message: String) : ProductDetailUiState
}