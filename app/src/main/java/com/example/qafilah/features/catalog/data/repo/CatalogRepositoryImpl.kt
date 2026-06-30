package com.example.qafilah.features.catalog.data.repo

import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.data.mapper.toDomain
import com.example.qafilah.features.catalog.data.mapper.toStoreCollection
import com.example.qafilah.features.catalog.domain.model.CollectionWithProducts
import com.example.qafilah.features.catalog.domain.model.ProductDetails
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CatalogRepositoryImpl(
    private val remoteDataSource: CatalogRemoteDataSource
) : CatalogRepository {
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

    override suspend fun getProductDetail(productId: String): Result<com.example.qafilah.features.product_detail.domain.model.ProductDetail> {
        throw UnsupportedOperationException("Moved to ProductDetailRepository")
    }

    override suspend fun getBestSellingProducts(limit: Int, after: String?): List<Product> {
        val networkResult = remoteDataSource.getBestSellingProducts(limit, after)
        return networkResult.map { it.toDomain() }
    }

    override suspend fun getCollections(limit: Int, after: String?): List<StoreCollection> {
        val response = remoteDataSource.getCollections(limit, after)
        return response.collections.edges.map { edge ->
            edge.node.toDomain()
        }
    }

    override suspend fun getProductsByCollection(id: String): CollectionWithProducts {

        val response = remoteDataSource.getProductsByCollection(id)

        val collectionData = response.collection ?: throw Exception("Collection not found")

        val storeCollection = collectionData.toStoreCollection()
        val products = collectionData.products.edges.map { it.node.toDomain() }

        return CollectionWithProducts(
            collectionInfo = storeCollection,
            products = products
        )
    }
}
