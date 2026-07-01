package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class AddCartItemUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(variantId: String, quantity: Int = 1) {
        val currentCart = repository.cartState.value

        if (currentCart == null) {
            repository.createCart(variantId, quantity)
        } else {
            repository.addItemToCart(variantId, quantity)
            repository.fetchCart()
        }
    }
}