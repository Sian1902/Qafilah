package com.example.qafilah.features.product_detail.domain.model

data class ProductDetail(
    val id: String,
    val title: String,
    val descriptionHtml: String,
    val images: List<ProductImage>,
    val variants: List<ProductVariant>,
    val metafields: Map<String, String>
) {
    fun getMetafield(key: String): String? = metafields[key]
}

data class ProductImage(
    val url: String,
    val altText: String?
)

data class ProductVariant(
    val id: String,
    val title: String,
    val price: String,
    val inventoryQuantity: Int?,
    val selectedOptions: List<OptionValue>
)

data class OptionValue(
    val name: String,
    val value: String
)
