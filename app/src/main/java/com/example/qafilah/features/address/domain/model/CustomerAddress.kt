package com.example.qafilah.features.address.domain.model

data class CustomerAddress(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val province: String?,
    val country: String?,
    val zip: String?,
    val phone: String?,
    val isDefault: Boolean = false
)
