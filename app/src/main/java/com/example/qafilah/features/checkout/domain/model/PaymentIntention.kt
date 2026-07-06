package com.example.qafilah.features.checkout.domain.model

data class PaymentIntention(
    val publicKey: String,
    val clientSecret: String,
    val orderReference: String
)