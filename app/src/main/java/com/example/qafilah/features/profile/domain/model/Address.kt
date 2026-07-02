package com.example.qafilah.features.profile.domain.model

data class Address(
    val id: String,
    val firstName: String,
    val lastName: String,
    val address1: String,
    val address2: String? = null,
    val city: String,
    val province: String? = null,
    val country: String,
    val zip: String,
    val phone: String
)