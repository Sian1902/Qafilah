package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository

class GetBestSellingUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(limit: Int, after: String?): List<Product> {
        return catalogRepository.getBestSellingProducts(limit, after)
    }
}