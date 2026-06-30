package com.example.qafilah.features.product_detail.domain.usecase

import com.example.qafilah.features.product_detail.domain.model.ProductDetail
import com.example.qafilah.features.product_detail.domain.repo.ProductDetailRepository

class GetProductDetailUseCase(
    private val repository: ProductDetailRepository
) {
    suspend operator fun invoke(productId: String): Result<ProductDetail> {
        return repository.getProductDetail(productId)
    }
}
