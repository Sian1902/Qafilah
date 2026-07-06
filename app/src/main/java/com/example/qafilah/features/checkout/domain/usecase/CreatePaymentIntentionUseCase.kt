package com.example.qafilah.features.checkout.domain.usecase

import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.model.PaymentIntention
import com.example.qafilah.features.checkout.domain.repo.PaymentRepository
import kotlin.math.roundToInt

class CreateCardPaymentIntentionUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(cart: CheckoutCart, totalEgp: Double): PaymentIntention {

        val amountInCents = (totalEgp * 100).roundToInt()

        return paymentRepository.createCardPaymentIntention(
            amountInCents = amountInCents,
            orderReference = cart.id
        )
    }
}