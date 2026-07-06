package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.model.PaymentIntention
import com.example.qafilah.features.checkout.domain.repo.PaymentRepository
import java.math.BigDecimal

class CreateCardPaymentIntentionUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(cart: CheckoutCart): PaymentIntention {
        val amountInCents = cart.cost.totalAmount.amount
            .multiply(BigDecimal(100))
            .toInt()

        return paymentRepository.createCardPaymentIntention(
            amountInCents = amountInCents,
            orderReference = cart.id
        )
    }
}