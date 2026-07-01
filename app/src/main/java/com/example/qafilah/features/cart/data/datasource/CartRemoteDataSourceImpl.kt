package com.example.qafilah.features.cart.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.storefront.AddCartLinesMutation
import com.example.qafilah.graphql.storefront.CreateCartMutation
import com.example.qafilah.graphql.storefront.GetCartQuery
import com.example.qafilah.graphql.storefront.RemoveCartLinesMutation
import com.example.qafilah.graphql.storefront.UpdateCartLinesMutation
import com.example.qafilah.graphql.storefront.type.CartInput
import com.example.qafilah.graphql.storefront.type.CartLineInput
import com.example.qafilah.graphql.storefront.type.CartLineUpdateInput

class CartRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : CartRemoteDataSource {

    override suspend fun getCart(cartId: String): GetCartQuery.Cart? {
        val data = safeApiCall {
            apolloClient.query(GetCartQuery(cartId = cartId)).execute()
        }
        return data.cart
    }

    override suspend fun createCart(variantId: String, quantity: Int): CreateCartMutation.Cart? {
        val data = safeApiCall {
            apolloClient.mutation(
                CreateCartMutation(
                    input = CartInput(
                        lines = Optional.present(
                            listOf(
                                CartLineInput(
                                    merchandiseId = variantId,
                                    quantity = Optional.present(quantity)
                                )
                            )
                        )
                    )
                )
            ).execute()
        }

        val userErrors = data.cartCreate?.userErrors
        if (!userErrors.isNullOrEmpty()) throw Exception(userErrors.first().message)

        return data.cartCreate?.cart
    }

    override suspend fun addItemToCart(cartId: String, variantId: String, quantity: Int) {
        val data = safeApiCall {
            apolloClient.mutation(
                AddCartLinesMutation(
                    cartId = cartId,
                    lines = listOf(
                        CartLineInput(
                            merchandiseId = variantId,
                            quantity = Optional.present(quantity)
                        )
                    )
                )
            ).execute()
        }

        val userErrors = data.cartLinesAdd?.userErrors
        if (!userErrors.isNullOrEmpty()) throw Exception(userErrors.first().message)
    }

    override suspend fun updateCartItem(cartId: String, lineId: String, quantity: Int) {
        val data = safeApiCall {
            apolloClient.mutation(
                UpdateCartLinesMutation(
                    cartId = cartId,
                    lines = listOf(
                        CartLineUpdateInput(
                            id = lineId,
                            quantity = Optional.present(quantity)
                        )
                    )
                )
            ).execute()
        }
        val userErrors = data.cartLinesUpdate?.userErrors
        if (!userErrors.isNullOrEmpty()) throw Exception(userErrors.first().message)
    }

    override suspend fun removeCartItem(cartId: String, lineId: String) {
        val data = safeApiCall {
            apolloClient.mutation(
                RemoveCartLinesMutation(
                    cartId = cartId,
                    lineIds = listOf(lineId)
                )
            ).execute()
        }

        val userErrors = data.cartLinesRemove?.userErrors
        if (!userErrors.isNullOrEmpty()) {
            throw Exception(userErrors.first().message)
        }
    }
}