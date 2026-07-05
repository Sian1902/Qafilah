package com.example.qafilah.features.checkout.domain.repo

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.checkout.domain.model.CheckoutCart

interface CheckoutRepository{
    suspend fun updateBuyerIdentity(
        cartId: String,
        address: ShippingAddress,
        phone: String
    ): Result<CheckoutCart>

    suspend fun updateDeliveryOption(
        cartId: String,
        deliveryGroupId: String,
        optionHandle: String
    ): Result<CheckoutCart>
}