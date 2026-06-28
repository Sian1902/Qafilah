package com.example.qafilah.features.catalog.data.repo

import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.domain.CatalogRepository
import com.example.qafilah.core.model.Product
import com.example.qafilah.graphql.admin.SearchProductsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(
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
}

fun SearchProductsQuery.Node.toDomain(): Product {
    return Product(
        id = this.id,
        title = this.title,
        vendor = this.vendor,
        productType = this.productType,
        imageUrl = this.featuredMedia?.preview?.image?.url as? String,
        priceAmount = this.priceRangeV2.minVariantPrice.amount.toString(),
        currencyCode = this.priceRangeV2.minVariantPrice.currencyCode.name
    )
}