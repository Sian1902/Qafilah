package com.example.qafilah.features.catalog.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional.Companion.presentIfNotNull
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.storefront.GetBestSellingProductsQuery
import com.example.qafilah.graphql.storefront.GetCollectionsQuery
import com.example.qafilah.graphql.storefront.GetProductQuery
import com.example.qafilah.graphql.storefront.GetProductsByCollectionQuery
import com.example.qafilah.graphql.storefront.SearchProductsQuery
import com.example.qafilah.graphql.storefront.type.ProductCollectionSortKeys

class CatalogRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : CatalogRemoteDataSource {

    override suspend fun searchProducts(query: String, limit: Int): List<SearchProductsQuery.OnProduct> {
        val data = safeApiCall {
            apolloClient.query(SearchProductsQuery(query = query, first = limit)).execute()
        }
        return data.search.edges.mapNotNull { it.node.onProduct }
    }

    override suspend fun getProduct(id: String): GetProductQuery.Product? {
        val data = safeApiCall {
            apolloClient.query(GetProductQuery(id = id)).execute()
        }
        return data.product
    }

    override suspend fun getBestSellingProducts(limit: Int, after: String?): List<GetBestSellingProductsQuery.Node> {
        val cursor = presentIfNotNull(after)
        val data = safeApiCall {
            apolloClient.query(GetBestSellingProductsQuery(first = limit, after = cursor)).execute()
        }
        return data.products.edges.map { it.node }
    }

    override suspend fun getCollections(limit: Int, after: String?): GetCollectionsQuery.Data {
        val cursor = presentIfNotNull(after)

        return safeApiCall {
            apolloClient.query(GetCollectionsQuery(first = limit, after = cursor)).execute()
        }
    }

    override suspend fun getProductsByCollection(id: String): GetProductsByCollectionQuery.Data {
        val defaultSort = com.apollographql.apollo.api.Optional.present(ProductCollectionSortKeys.CREATED)

        return safeApiCall {
            apolloClient.query(
                GetProductsByCollectionQuery(
                    id = id,
                    first = 20,
                    after = com.apollographql.apollo.api.Optional.absent(),
                    sortKey = defaultSort,
                    reverse = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()
        }
    }
}