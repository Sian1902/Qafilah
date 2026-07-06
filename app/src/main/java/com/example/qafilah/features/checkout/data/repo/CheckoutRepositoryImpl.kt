package com.example.qafilah.features.checkout.data.repo

import com.apollographql.apollo.api.Optional
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSource
import com.example.qafilah.features.checkout.data.mapper.toAdminLineItemInput
import com.example.qafilah.features.checkout.data.mapper.toAdminMailingAddressInput
import com.example.qafilah.features.checkout.data.mapper.toDeliveryAddressInput
import com.example.qafilah.features.checkout.data.mapper.toDomain
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository
import com.example.qafilah.graphql.admin.type.DraftOrderInput
import com.example.qafilah.graphql.admin.type.ShippingLineInput

class CheckoutRepositoryImpl(
    private val remoteDataSource: CheckoutRemoteDataSource
): CheckoutRepository {
    override suspend fun updateBuyerIdentity(
        cartId: String,
        address: ShippingAddress,
        phone: String
    ): Result<CheckoutCart> {

        val shopifyAddressInput = address.toDeliveryAddressInput(phone = phone)

        return remoteDataSource.updateBuyerIdentity(cartId, shopifyAddressInput).map { fragment ->
            fragment.toDomain()
        }
    }

    override suspend fun updateDeliveryOption(
        cartId: String,
        deliveryGroupId: String,
        optionHandle: String
    ): Result<CheckoutCart> {
        return remoteDataSource.updateDeliveryOption(cartId, deliveryGroupId, optionHandle).map { fragment ->
            fragment.toDomain()
        }
    }

    override suspend fun createAndCompleteOrder(
        cart: CheckoutCart,
        customerId: String,
        customerFirstName: String,
        customerLastName: String,
        customerPhone: String?,
        address: ShippingAddress,
        selectedDeliveryHandle: String
    ): Result<String> {

        val selectedShipping = cart.deliveryGroups.firstOrNull()
            ?.deliveryOptions?.find { it.handle == selectedDeliveryHandle }
            ?: return Result.failure(Exception("Selected shipping option not found in cart"))

        val adminLineItems = cart.lines.map { it.toAdminLineItemInput() }

        val tempUser = AppUser(
            id = customerId,
            email = null,
            firstName = customerFirstName,
            lastName = customerLastName,
            phone = customerPhone
        )
        val adminShippingAddress = address.toAdminMailingAddressInput(tempUser)

        val input = DraftOrderInput(
            customerId = Optional.present(customerId),
            lineItems = Optional.present(adminLineItems),
            shippingAddress = Optional.present(adminShippingAddress),
            shippingLine = Optional.present(
                ShippingLineInput(
                    title = Optional.present(selectedShipping.title),
                    price = Optional.present(selectedShipping.estimatedCost.amount.toString())
                )
            )
        )

        return remoteDataSource.createDraftOrder(input).fold(
            onSuccess = { draftOrderId ->
                remoteDataSource.completeDraftOrder(draftOrderId)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}