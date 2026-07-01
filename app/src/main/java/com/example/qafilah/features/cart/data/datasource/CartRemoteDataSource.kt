package com.example.qafilah.features.cart.data.datasource

import com.example.qafilah.graphql.storefront.GetCartQuery

interface CartRemoteDataSource {
    suspend fun getCart(cartId: String): GetCartQuery.Cart?
    suspend fun updateCartItem(cartId: String ,lineId: String, quantity: Int)
    suspend fun removeCartItem(cartId: String ,lineId: String)
}