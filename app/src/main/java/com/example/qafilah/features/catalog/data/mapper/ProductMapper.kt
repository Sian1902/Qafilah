package com.example.qafilah.features.catalog.data.mapper

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.ProductVariant
import com.example.qafilah.graphql.admin.GetProductQuery
import com.example.qafilah.graphql.admin.SearchProductsQuery


fun GetProductQuery.Product.toDomain(): ProductDetails {
    return ProductDetails(
        id = this.id,
        title = this.title,
        descriptionHtml = this.descriptionHtml as String?,

        images = this.images.edges.mapNotNull { it.node.url as? String },

        variants = this.variants.edges.map { edge ->
            val node = edge.node
            ProductVariant(
                id = node.id,
                title = node.title,
                price = node.price.toString(),
                inventoryQuantity = node.inventoryQuantity,

                options = node.selectedOptions.associate { option ->
                    option.name to option.value
                }
            )
        }
    )
}

fun SearchProductsQuery.Node.toDomain(): Product {
    return Product(
        id = this.id,
        title = this.title,
        vendor = this.vendor,
        productType = this.productType,
        imageUrl = this.featuredMedia?.preview?.image?.url as? String,
        priceAmount = this.priceRangeV2.minVariantPrice.amount.toString(),
        currencyCode = this.priceRangeV2.minVariantPrice.currencyCode.name
    )
}