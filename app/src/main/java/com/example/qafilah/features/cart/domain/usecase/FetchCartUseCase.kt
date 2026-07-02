package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class FetchCartUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke() {
        repository.fetchCart()
    }
}