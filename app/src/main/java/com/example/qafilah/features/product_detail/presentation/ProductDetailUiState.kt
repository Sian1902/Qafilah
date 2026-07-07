package com.example.qafilah.features.product_detail.presentation

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState

    data class Success(
        val product: ProductDetailUiModel,
        val isAddingToCart: Boolean = false,
        val addToCartError: String? = null
    ) : ProductDetailUiState

    data class Error(val message: String) : ProductDetailUiState
}

data class ProductDetailLabels(
    val defaultCollectionLabel: String,
    val inStockTemplate: String,
    val outOfStockText: String,
    val ratingLabelTemplate: String
)
