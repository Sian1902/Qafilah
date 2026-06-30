package com.example.qafilah.features.product_detail.data.datasource

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.qafilah.graphql.storefront.GetProductQuery
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.core.network.ShopifyClient

class ProductDetailRemoteDataSource(
    private val apolloClient: ApolloClient = ShopifyClient.instance
) {
    suspend fun getProductDetail(id: String): GetProductQuery.Product? {
        val response = safeApiCall<GetProductQuery.Data> {
            apolloClient.query(GetProductQuery(id = id)).execute()
        }
        return response.product
    }
}
