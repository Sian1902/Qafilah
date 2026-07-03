package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.repo.CatalogRepository

class GetProductTypesUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(limit: Int): List<String> {
        return catalogRepository.getProductTypes(limit)
    }
}