package com.example.qafilah.features.checkout.data.repo

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSource
import com.example.qafilah.features.checkout.data.mapper.toDeliveryAddressInput
import com.example.qafilah.features.checkout.data.mapper.toDomain
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository

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
}