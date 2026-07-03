package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository

class GetProductsByTypeUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(productType: String, limit: Int): List<Product> {
        return catalogRepository.getProductsByType(productType, limit)
    }
}