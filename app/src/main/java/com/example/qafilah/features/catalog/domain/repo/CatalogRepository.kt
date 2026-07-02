package com.example.qafilah.features.catalog.domain.repo

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.CollectionWithProducts
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.StoreCollection

interface CatalogRepository {
    suspend fun searchProducts(query: String, limit: Int): Result<List<Product>>
    suspend fun getProductDetails(productId: String): Result<ProductDetails>
    suspend fun getBestSellingProducts(limit: Int, after: String?): List<Product>
    suspend fun getCollections(limit: Int, after: String?): List<StoreCollection>
    suspend fun getProductsByCollection(id: String): CollectionWithProducts
}