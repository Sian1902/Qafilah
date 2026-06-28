package com.example.qafilah.features.catalog.domain.model

data class ProductDetails(
    val id: String,
    val title: String,
    val descriptionHtml: String?,
    val images: List<String>,
    val variants: List<ProductVariant>
)

data class ProductVariant(
    val id: String,
    val title: String,
    val price: String,
    val inventoryQuantity: Int?,
    val options: Map<String, String> // e.g., {"Size": "Large", "Color": "Red"}
)