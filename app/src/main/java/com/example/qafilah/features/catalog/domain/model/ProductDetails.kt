package com.example.qafilah.features.catalog.domain.model


data class ProductDetails(
    val id: String,
    val title: String,
    val vendor: String,
    val productType: String,
    val tags: List<String>,
    val description: String?,
    val images: List<String>,
    val rating: Double?,
    val ratingCount: Int?,
    val variants: List<ProductVariant>
)

data class ProductVariant(
    val id: String,
    val title: String,
    val price: String,
    val compareAtPrice: String?,
    val inventoryQuantity: Int?,
    val options: Map<String, String>
)