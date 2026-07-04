package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository

class UpdateDeliveryOptionUseCase(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(
        cartId: String,
        deliveryGroupId: String,
        optionHandle: String
    ): Result<CheckoutCart> {
        return checkoutRepository.updateDeliveryOption(
            cartId = cartId,
            deliveryGroupId = deliveryGroupId,
            optionHandle = optionHandle
        )
    }
}