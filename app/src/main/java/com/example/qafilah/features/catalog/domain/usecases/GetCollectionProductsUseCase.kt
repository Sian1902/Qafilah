package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.model.CollectionWithProducts
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository

class GetCollectionProductsUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(id: String): CollectionWithProducts {
        return catalogRepository.getProductsByCollection(id)
    }
}