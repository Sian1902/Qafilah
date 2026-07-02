package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.repo.CartRepository

class ApplyDiscountUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(newCode: String) {
        val currentCart = repository.cartState.value ?: throw Exception("Cart is empty")

        val currentValidCodes = currentCart.appliedDiscounts.map { it.code }

        if (currentValidCodes.any { it.equals(newCode, ignoreCase = true) }) {
            throw Exception("Code is already applied")
        }

        val codesToSend = currentValidCodes + newCode.uppercase()
        repository.updateDiscountCodes(codesToSend)

        val updatedCart = repository.cartState.value ?: return

        val addedCodeResult = updatedCart.discountCodes.find {
            it.code.equals(newCode, ignoreCase = true)
        }

        if (addedCodeResult != null && !addedCodeResult.applicable) {

            repository.updateDiscountCodes(currentValidCodes)

            throw Exception("The discount code '${newCode.uppercase()}' is invalid or expired.")
        }
    }
}