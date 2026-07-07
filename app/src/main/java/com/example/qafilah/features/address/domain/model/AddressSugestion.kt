package com.example.qafilah.features.address.domain.model

data class AddressSuggestion(
    val displayName: String,
    val street: String,
    val city: String,
    val province: String,
    val country: String,
    val zipCode: String
)