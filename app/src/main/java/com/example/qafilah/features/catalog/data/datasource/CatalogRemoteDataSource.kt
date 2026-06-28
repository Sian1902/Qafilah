package com.example.qafilah.features.catalog.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.graphql.admin.GetProductQuery
import com.example.qafilah.graphql.admin.SearchProductsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface CatalogRemoteDataSource {
    suspend fun searchProducts(query: String, limit: Int): List<SearchProductsQuery.Node>
    suspend fun getProduct(id: String): GetProductQuery.Product?
}

