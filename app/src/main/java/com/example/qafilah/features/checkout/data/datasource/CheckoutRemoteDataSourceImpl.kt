package com.example.qafilah.features.checkout.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.graphql.admin.DraftOrderCompleteMutation
import com.example.qafilah.graphql.admin.DraftOrderCreateMutation
import com.example.qafilah.graphql.admin.type.DraftOrderInput
import com.example.qafilah.graphql.storefront.CartBuyerIdentityUpdateMutation
import com.example.qafilah.graphql.storefront.CartSelectedDeliveryOptionsUpdateMutation
import com.example.qafilah.graphql.storefront.fragment.CheckoutCartDetails
import com.example.qafilah.graphql.storefront.type.CartBuyerIdentityInput
import com.example.qafilah.graphql.storefront.type.CartSelectedDeliveryOptionInput
import com.example.qafilah.graphql.storefront.type.DeliveryAddressInput


class CheckoutRemoteDataSourceImpl(
    private val storefrontClient: ApolloClient,
    private val adminClient: ApolloClient
): CheckoutRemoteDataSource {
    override suspend fun updateBuyerIdentity(
        cartId: String,
        addressInput: DeliveryAddressInput
    ): Result<CheckoutCartDetails> {
        return try {
            val response = storefrontClient.mutation(
                CartBuyerIdentityUpdateMutation(
                    cartId = cartId,
                    buyerIdentity = CartBuyerIdentityInput(
                        deliveryAddressPreferences = Optional.present(listOf(addressInput))
                    )
                )
            ).execute()

            val userErrors = response.data?.cartBuyerIdentityUpdate?.userErrors
            if (!userErrors.isNullOrEmpty()) {
                return Result.failure(Exception(userErrors.first().message))
            }

            val fragment = response.data?.cartBuyerIdentityUpdate?.cart?.checkoutCartDetails
                ?: return Result.failure(Exception("Cart data is missing"))

            Result.success(fragment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDeliveryOption(
        cartId: String,
        deliveryGroupId: String,
        optionHandle: String
    ): Result<CheckoutCartDetails> {
        return try {
            val response = storefrontClient.mutation(
                CartSelectedDeliveryOptionsUpdateMutation(
                    cartId = cartId,
                    selectedDeliveryOptions = listOf(
                        CartSelectedDeliveryOptionInput(
                            deliveryGroupId = deliveryGroupId,
                            deliveryOptionHandle = optionHandle
                        )
                    )
                )
            ).execute()

            val userErrors = response.data?.cartSelectedDeliveryOptionsUpdate?.userErrors
            if (!userErrors.isNullOrEmpty()) {
                return Result.failure(Exception(userErrors.first().message))
            }

            val fragment = response.data?.cartSelectedDeliveryOptionsUpdate?.cart?.checkoutCartDetails
                ?: return Result.failure(Exception("Cart data is missing"))

            Result.success(fragment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createDraftOrder(input: DraftOrderInput): Result<String> {
        return try {
            val data = safeApiCall {
                adminClient.mutation(DraftOrderCreateMutation(input)).execute()
            }

            val userErrors = data.draftOrderCreate?.userErrors
            if (!userErrors.isNullOrEmpty()) {
                return Result.failure(Exception(userErrors.first().message))
            }

            val draftOrderId = data.draftOrderCreate?.draftOrder?.id
                ?: return Result.failure(Exception("Draft Order ID is null"))

            Result.success(draftOrderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: String): Result<String> {
        return try {
            val data = safeApiCall {
                adminClient.mutation(
                    DraftOrderCompleteMutation(
                        id = draftOrderId,
                        paymentPending = false
                    )
                ).execute()
            }

            val userErrors = data.draftOrderComplete?.userErrors
            if (!userErrors.isNullOrEmpty()) {
                return Result.failure(Exception(userErrors.first().message))
            }

            val finalOrderId = data.draftOrderComplete?.draftOrder?.order?.id
                ?: return Result.failure(Exception("Final Order ID is null"))

            Result.success(finalOrderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}