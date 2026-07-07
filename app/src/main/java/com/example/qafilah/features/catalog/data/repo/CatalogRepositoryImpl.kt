package com.example.qafilah.features.catalog.data.repo

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.data.datasource.ReviewsRemoteDataSource
import com.example.qafilah.features.catalog.data.mapper.toDomain
import com.example.qafilah.features.catalog.data.mapper.toStoreCollection
import com.example.qafilah.features.catalog.domain.model.CollectionWithProducts
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.model.SubmitReviewParams
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CatalogRepositoryImpl(
    private val catalogRemoteDataSource: CatalogRemoteDataSource,
    private val reviewsRemoteDataSource: ReviewsRemoteDataSource
) : CatalogRepository {
    override suspend fun searchProducts(
        query: String,
        limit: Int
    ): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val products = catalogRemoteDataSource.searchProducts(query, limit)

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

                val productData = catalogRemoteDataSource.getProduct(formattedId)
                    ?: throw Exception("Product not found")

                Result.success(productData.toDomain())

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    override suspend fun getBestSellingProducts(limit: Int, after: String?): List<Product> {
        val networkResult = catalogRemoteDataSource.getBestSellingProducts(limit, after)
        return networkResult.map { it.toDomain() }
    }

    override suspend fun getCollections(limit: Int, after: String?): List<StoreCollection> {
        val response = catalogRemoteDataSource.getCollections(limit, after)
        return response.collections.edges.map { edge ->
            edge.node.toDomain()
        }
    }

    override suspend fun getProductsByCollection(id: String): CollectionWithProducts {

        val response = catalogRemoteDataSource.getProductsByCollection(id)

        val collectionData = response.collection ?: throw Exception("Collection not found")

        val storeCollection = collectionData.toStoreCollection()
        val products = collectionData.products.edges.map { it.node.toDomain() }

        return CollectionWithProducts(
            collectionInfo = storeCollection,
            products = products
        )
    }

    override suspend fun getProductTypes(limit: Int): List<String> =
        catalogRemoteDataSource.getProductTypes(limit)

    override suspend fun getProductsByType(productType: String, limit: Int): List<Product> =
        catalogRemoteDataSource.getProductsByType(productType, limit).map { it.toDomain() }

    override suspend fun submitProductReview(params: SubmitReviewParams): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val formattedId = if (params.productId.startsWith("gid://")) {
                    params.productId
                } else {
                    "gid://shopify/Product/${params.productId}"
                }

                val newMetaobjectId = reviewsRemoteDataSource.createReviewMetaobject(
                    productId = formattedId,
                    name = params.customerName,
                    rating = params.rating,
                    title = params.title,
                    body = params.body,
                    date = params.dateString
                ) ?: throw Exception("Failed to generate review ID")

                val currentJsonArrayStr = reviewsRemoteDataSource.getProductReviewMetafieldValue(formattedId)

                val currentIds = if (currentJsonArrayStr.isNullOrBlank()) {
                    emptyList<String>()
                } else {
                    currentJsonArrayStr
                        .removePrefix("[")
                        .removeSuffix("]")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                        .filter { it.isNotBlank() }
                }

                val updatedIds = currentIds + newMetaobjectId

                val newJsonArrayStr = updatedIds.joinToString(
                    separator = ",",
                    prefix = "[",
                    postfix = "]"
                ) { "\"$it\"" }

                reviewsRemoteDataSource.setProductReviewMetafield(formattedId, newJsonArrayStr)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
