package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository

class UpdateBuyerIdentityUseCase(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(cartId: String, addressInput: ShippingAddress): Result<CheckoutCart> {
        val temporaryPhoneNumber = "+12025550134"

        return checkoutRepository.updateBuyerIdentity(
            cartId = cartId,
            address = addressInput,
            phone = temporaryPhoneNumber
        )
    }
}