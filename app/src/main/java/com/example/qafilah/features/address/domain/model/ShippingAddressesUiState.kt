package com.example.qafilah.features.address.domain.model

data class ShippingAddressesUiState(
    val addresses: List<ShippingAddress> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)