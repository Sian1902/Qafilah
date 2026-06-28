package com.example.qafilah.features.catalog.domain.repo

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.ProductDetails

interface CatalogRepository {
    suspend fun searchProducts(query: String, limit: Int): Result<List<Product>>
    suspend fun getProductDetails(productId: String): Result<ProductDetails>
}