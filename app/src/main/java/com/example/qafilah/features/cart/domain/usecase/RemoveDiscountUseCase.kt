package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class RemoveDiscountUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(codeToRemove: String) {
        val currentCart = repository.cartState.value ?: return

        val remainingCodes = currentCart.appliedDiscounts
            .map { it.code }
            .filter { !it.equals(codeToRemove, ignoreCase = true) }

        repository.updateDiscountCodes(remainingCodes)
    }
}