package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class RemoveCartItemUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(lineId: String) {
        val currentCart = repository.cartState.value
            ?: throw Exception("Cart is not loaded")

        val fallbackState = currentCart

        val updatedLines = currentCart.lines.filter { it.id != lineId }

        val optimisticCart = currentCart.copy(
            lines = updatedLines,
            totalQuantity = updatedLines.sumOf { it.quantity }
        )

        repository.updateLocalCartState(optimisticCart)

        try {
            repository.removeCartItemRemote(lineId)

            repository.fetchCart()

        } catch (e: Exception) {
            repository.updateLocalCartState(fallbackState)
            throw e
        }
    }
}