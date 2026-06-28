package com.example.qafilah.features.catalog.domain.model


import com.example.qafilah.core.model.Product

data class CollectionWithProducts(
    val collectionInfo: StoreCollection,
    val products: List<Product>
)