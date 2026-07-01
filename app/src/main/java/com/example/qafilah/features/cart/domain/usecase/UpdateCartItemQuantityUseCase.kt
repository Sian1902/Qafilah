package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class UpdateCartItemQuantityUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(lineId: String, newQuantity: Int) {
        val currentCart = repository.cartState.value
            ?: throw Exception("Cart is not loaded")

        val fallbackState = currentCart

        val updatedLines = currentCart.lines.map { line ->
            if (line.id == lineId) {
                line.copy(quantity = newQuantity)
            } else {
                line
            }
        }

        val optimisticCart = currentCart.copy(
            lines = updatedLines,
        totalQuantity = updatedLines.sumOf { it.quantity }
        )

        repository.updateLocalCartState(optimisticCart)

        try {
            repository.updateCartItemRemote(lineId, newQuantity)

            repository.fetchCart()

        } catch (e: Exception) {
            repository.updateLocalCartState(fallbackState)
            throw e
        }
    }
}