package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository
import com.example.qafilah.features.checkout.presentation.shared.CheckoutMocks
import kotlinx.coroutines.delay

class UpdateBuyerIdentityUseCase(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(cartId: String, addressInput: Any): Result<CheckoutCart> {
        delay(1500)
        return Result.success(CheckoutMocks.cartWithAddress)
    }
}

