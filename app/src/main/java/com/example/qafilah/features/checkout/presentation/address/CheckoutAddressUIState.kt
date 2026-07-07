package com.example.qafilah.features.checkout.presentation.address

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser

data class CheckoutAddressUIState(
    val isLoadingAddresses: Boolean = false,
    val isSubmitting: Boolean = false,
    val addresses: List<ShippingAddress> = emptyList(),
    val selectedAddressId: String? = null,
    val appUser: AppUser? = null,
    val error: String? = null,
    val errorMessageResId: Int? = null
)