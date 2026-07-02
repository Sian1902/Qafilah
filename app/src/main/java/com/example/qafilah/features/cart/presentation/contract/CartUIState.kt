package com.example.qafilah.features.cart.presentation.contract

import com.example.qafilah.features.cart.domain.model.StoreCart

data class CartUIState(
    val isLoading: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val errorMessage: String? = null,
    val cart: StoreCart? = null,

    val discountInput: String = "",
    val isApplyingDiscount: Boolean = false,
    val discountError: String? = null
)