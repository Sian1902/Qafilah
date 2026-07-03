package com.example.qafilah.features.catalog.data.datasource

import com.example.qafilah.graphql.storefront.GetBestSellingProductsQuery
import com.example.qafilah.graphql.storefront.GetCollectionsQuery
import com.example.qafilah.graphql.storefront.GetProductQuery
import com.example.qafilah.graphql.storefront.GetProductsByCollectionQuery
import com.example.qafilah.graphql.storefront.GetProductsByTypeQuery
import com.example.qafilah.graphql.storefront.SearchProductsQuery

interface CatalogRemoteDataSource {
    suspend fun searchProducts(query: String, limit: Int): List<SearchProductsQuery.OnProduct>
    suspend fun getProduct(id: String): GetProductQuery.Product?
    suspend fun getBestSellingProducts(
        limit: Int,
        after: String?
    ): List<GetBestSellingProductsQuery.Node>

    suspend fun getCollections(limit: Int, after: String?): GetCollectionsQuery.Data
    suspend fun getProductsByCollection(id: String): GetProductsByCollectionQuery.Data
    suspend fun getProductTypes(limit: Int): List<String>

    suspend fun getProductsByType(productType: String, limit: Int): List<GetProductsByTypeQuery.Node>

}

