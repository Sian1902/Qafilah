package com.example.qafilah.features.product_detail.data.repo

import com.example.qafilah.features.product_detail.data.datasource.ProductDetailRemoteDataSource
import com.example.qafilah.features.product_detail.data.mapper.ProductDetailMapper
import com.example.qafilah.features.product_detail.domain.model.ProductDetail
import com.example.qafilah.features.product_detail.domain.repo.ProductDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductDetailRepositoryImpl(
    private val remoteDataSource: ProductDetailRemoteDataSource
) : ProductDetailRepository {
    override suspend fun getProductDetail(productId: String): Result<ProductDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val formattedId = if (productId.startsWith("gid://")) {
                    productId
                } else {
                    "gid://shopify/Product/$productId"
                }

                val productData = remoteDataSource.getProductDetail(formattedId)
                    ?: throw Exception("Product not found")

                Result.success(ProductDetailMapper.toDomain(productData))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
