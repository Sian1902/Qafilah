package com.example.qafilah.features.product_detail.presentation

import com.example.qafilah.features.product_detail.domain.model.ProductDetail
import com.example.qafilah.features.product_detail.domain.model.ProductVariant

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState
    
    data class Success(
        val product: ProductDetail,
        val selectedVariant: ProductVariant,
        val selectedOptions: Map<String, String>,
        val isFavorite: Boolean
    ) : ProductDetailUiState
    
    data class Error(val message: String) : ProductDetailUiState
}
