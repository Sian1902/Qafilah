package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository

class GetCollectionsUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(limit: Int, after: String?): List<StoreCollection> {
        return catalogRepository.getCollections(limit, after)
    }
}