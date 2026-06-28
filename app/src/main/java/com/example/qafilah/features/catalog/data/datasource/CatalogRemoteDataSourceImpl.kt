package com.example.qafilah.features.catalog.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.graphql.admin.GetProductQuery
import com.example.qafilah.graphql.admin.SearchProductsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CatalogRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : CatalogRemoteDataSource {
    override suspend fun searchProducts(searchQuery: String, limit: Int): List<SearchProductsQuery.Node> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apolloClient
                    .query(SearchProductsQuery(query = searchQuery, first = limit))
                    .execute()

                if (response.hasErrors()) {
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "GraphQL Error"
                    throw Exception(errorMessage)
                }

                response.data?.products?.edges?.mapNotNull { it.node } ?: emptyList()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun getProduct(id: String): GetProductQuery.Product? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apolloClient
                    .query(GetProductQuery(id = id))
                    .execute()

                if (response.hasErrors()) {
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "GraphQL Error"
                    throw Exception(errorMessage)
                }

                response.data?.product
            } catch (e: Exception) {
                throw e
            }
        }
    }

}