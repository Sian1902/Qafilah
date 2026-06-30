package com.example.qafilah.features.product_detail.domain.repo

import com.example.qafilah.features.product_detail.domain.model.ProductDetail

interface ProductDetailRepository {
    suspend fun getProductDetail(productId: String): Result<ProductDetail>
}
