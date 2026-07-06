package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class ClearCartUseCase(
    private val cartRepository: CartRepository
){
    suspend operator fun invoke() {
        cartRepository.clearCart()
    }
}