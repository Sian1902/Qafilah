package com.example.qafilah.features.cart.data.datasource

import com.apollographql.apollo.ApolloClient
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.storefront.GetCartQuery

class CartRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
): CartRemoteDataSource  {
    override suspend fun getCart(cartId: String): GetCartQuery.Cart? {
        val data = safeApiCall{
            apolloClient.query(GetCartQuery(cartId = cartId)).execute()
        }
        return data.cart
    }

    override suspend fun updateCartItem(
        cartId: String,
        lineId: String,
        quantity: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun removeCartItem(cartId: String, lineId: String) {
        TODO("Not yet implemented")
    }
}