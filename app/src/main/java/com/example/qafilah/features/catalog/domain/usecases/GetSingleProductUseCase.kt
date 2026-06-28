package com.example.qafilah.features.catalog.domain.usecases

import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository

class GetSingleProductUseCase(
    private val homeRepository: CatalogRepository
) {
    suspend operator fun invoke(productId: String): Result<ProductDetails> {
        return homeRepository.getProductDetails(productId)
    }
}