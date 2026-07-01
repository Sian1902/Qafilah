package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.repo.CartRepository

class GetCartUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): StoreCart? {
        return cartRepository.getCart()
    }
}
