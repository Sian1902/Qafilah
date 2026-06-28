package com.example.qafilah.features.catalog.domain

import com.example.qafilah.core.model.Product

interface CatalogRepository {
    suspend fun searchProducts(query: String, limit: Int): Result<List<Product>>
}