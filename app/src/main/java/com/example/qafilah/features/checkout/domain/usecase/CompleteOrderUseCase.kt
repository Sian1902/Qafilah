package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository

class CompleteOrderUseCase(
    private val repository: CheckoutRepository
) {
    suspend operator fun invoke(
        cart: CheckoutCart,
        user: AppUser,
        address: ShippingAddress,
        selectedDeliveryHandle: String
    ): Result<String> {
        return repository.createAndCompleteOrder(
            cart = cart,
            customerId = user.id,
            customerFirstName = user.firstName ?: "",
            customerLastName = user.lastName ?: "",
            customerPhone = user.phone,
            address = address,
            selectedDeliveryHandle = selectedDeliveryHandle
        )
    }
}