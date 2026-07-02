package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class AddCartItemUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(variantId: String, quantity: Int = 1) {
        if (repository.hasActiveCart()) {
            repository.addItemToCart(variantId, quantity)
        } else {
            repository.createCart(variantId, quantity)
        }

        repository.fetchCart()
    }
}