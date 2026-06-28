package com.example.qafilah.features.catalog.data.mapper

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.ProductVariant
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.graphql.storefront.GetBestSellingProductsQuery
import com.example.qafilah.graphql.storefront.GetProductQuery
import com.example.qafilah.graphql.storefront.GetProductsByCollectionQuery
import com.example.qafilah.graphql.storefront.SearchProductsQuery

fun GetProductQuery.Product.toDomain(): ProductDetails {
    return ProductDetails(
        id = this.id,
        title = this.title,
        vendor = this.vendor,
        productType = this.productType,
        tags = this.tags,
        description = this.description,
        images = this.images.edges.map { it.node.url.toString() },

        rating = this.metafields.find { it?.key == "rating" }?.value?.toDoubleOrNull(),
        ratingCount = this.metafields.find { it?.key == "rating_count" }?.value?.toIntOrNull(),

        variants = this.variants.edges.map { edge ->
            val node = edge.node
            ProductVariant(
                id = node.id,
                title = node.title,
                price = node.price.amount.toString(),
                compareAtPrice = node.compareAtPrice?.amount?.toString(),
                inventoryQuantity = node.quantityAvailable,

                options = node.selectedOptions.associate { option ->
                    option.name to option.value
                }
            )
        }
    )
}

fun SearchProductsQuery.OnProduct.toDomain(): Product {
    return Product(
        id = this.id,
        title = this.title,
        vendor = this.vendor,

        imageUrl = this.images.edges.firstOrNull()?.node?.url?.toString(),

        priceAmount = this.priceRange.minVariantPrice.amount.toString(),
        currencyCode = this.priceRange.minVariantPrice.currencyCode.toString()
    )
}

fun GetBestSellingProductsQuery.Node.toDomain(): Product {
    return Product(
        id = this.id,
        title = this.title,
        vendor = this.vendor,
        imageUrl = this.images.edges.firstOrNull()?.node?.url?.toString(),
        priceAmount = this.priceRange.minVariantPrice.amount.toString(),
        currencyCode = this.priceRange.minVariantPrice.currencyCode.toString()
    )
}

fun GetProductsByCollectionQuery.Node.toDomain(): Product {
    return Product(
        id = this.id,
        title = this.title,
        vendor = this.vendor,
        imageUrl = this.images.edges.firstOrNull()?.node?.url?.toString(),
        priceAmount = this.priceRange.minVariantPrice.amount.toString(),
        currencyCode = this.priceRange.minVariantPrice.currencyCode.toString()
    )
}


fun GetProductsByCollectionQuery.Collection.toStoreCollection(): StoreCollection {
    return StoreCollection(
        id = this.id,
        title = this.title,
        handle = this.handle,
        description = this.description,
        imageUrl = this.image?.url?.toString()
    )
}