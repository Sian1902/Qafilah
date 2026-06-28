package com.example.qafilah.features.catalog.data.repo

import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.data.mapper.toDomain
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.ProductVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CatalogRepositoryImpl(
    private val remoteDataSource: CatalogRemoteDataSource
) : CatalogRepository{
    override suspend fun searchProducts(
        query: String,
        limit: Int
    ): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val products = remoteDataSource.searchProducts(query, limit)

                Result.success(products.map { it.toDomain() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getProductDetails(productId: String): Result<ProductDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val formattedId = if (productId.startsWith("gid://")) {
                    productId
                } else {
                    "gid://shopify/Product/$productId"
                }

                val productData = remoteDataSource.getProduct(formattedId)
                    ?: throw Exception("Product not found")

                Result.success(productData.toDomain())

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}
