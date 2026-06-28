package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.CatalogRepository
import com.example.qafilah.core.model.Product

class GetProductsUseCase(
    private val homeRepository: CatalogRepository
) {
    suspend operator fun invoke(
        query: String,
        limit: Int
    ): Result<List<Product>> {
        return homeRepository.searchProducts(query, limit)
    }
}