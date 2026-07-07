package com.example.qafilah.features.product_detail.presentation

data class ProductDetailUiModel(
    val id: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val tag: String,
    val rating: Double?,
    val reviewCount: Int?,
    val ratingLabel: String,
    val displayPrice: String,
    val stockText: String,
    val stockColorInt: Int,
    val isInStock: Boolean,
    val optionGroups: Map<String, List<String>>,
    val selectedOptions: Map<String, String>,
    val selectedVariantId: String,
    val isFavorite: Boolean
)
