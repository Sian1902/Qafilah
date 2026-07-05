package com.example.qafilah.features.checkout.presentation.summary

import com.example.qafilah.features.checkout.domain.model.CheckoutCart

data class CheckoutSummaryUIState(
    val isRecalculating: Boolean = false,
    val cart: CheckoutCart? = null,
    val selectedDeliveryHandle: String? = null
)

fun buildSummaryUiState(
    cart: CheckoutCart?,
    isRecalculating: Boolean,
    localSelectedHandle: String?
): CheckoutSummaryUIState {
    return CheckoutSummaryUIState(
        isRecalculating = isRecalculating,
        cart = cart,
        selectedDeliveryHandle = localSelectedHandle ?: cart?.defaultShippingOption?.handle
    )
}