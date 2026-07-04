package com.example.qafilah.features.checkout.data.datasource

import com.example.qafilah.graphql.admin.type.DraftOrderInput
import com.example.qafilah.graphql.storefront.fragment.CheckoutCartDetails
import com.example.qafilah.graphql.storefront.type.DeliveryAddressInput

interface CheckoutRemoteDataSource {
    suspend fun updateBuyerIdentity(
        cartId: String,
        addressInput: DeliveryAddressInput
    ): Result<CheckoutCartDetails>

    suspend fun updateDeliveryOption(
        cartId: String,
        deliveryGroupId: String,
        optionHandle: String
    ): Result<CheckoutCartDetails>

    suspend fun createDraftOrder(input: DraftOrderInput): Result<String>

    suspend fun completeDraftOrder(draftOrderId: String): Result<String>
}