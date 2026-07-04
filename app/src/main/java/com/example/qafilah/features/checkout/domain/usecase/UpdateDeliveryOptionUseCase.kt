package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository
import com.example.qafilah.features.checkout.presentation.shared.CheckoutMocks
import kotlinx.coroutines.delay

class UpdateDeliveryOptionUseCase(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(
        cartId: String,
        deliveryGroupId: String,
        optionHandle: String
    ): Result<CheckoutCart> {
        delay(1000)

        val updatedCart = if (optionHandle == "b82c208d77cc615cb38b955c068c84cc") {
            CheckoutMocks.cartWithExpressShipping
        } else {
            CheckoutMocks.cartWithAddress
        }
        return Result.success(updatedCart)
    }
}