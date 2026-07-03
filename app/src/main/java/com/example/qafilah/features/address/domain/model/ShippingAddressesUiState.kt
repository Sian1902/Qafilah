package com.example.qafilah.features.address.domain.model

data class ShippingAddressesUiState(
    val addresses: List<ShippingAddress> = emptyList(),
    val isLoading: Boolean = false,
    val isOperationInProgress: Boolean = false,
    val operationSuccess: Boolean? = null,
    val error: String? = null
)