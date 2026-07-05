package com.example.qafilah.features.checkout.domain.repo

import com.example.qafilah.features.checkout.domain.model.PaymentIntention

interface PaymentRepository {
    suspend fun createCardPaymentIntention(
        amountInCents: Int,
        orderReference: String
    ): PaymentIntention
}