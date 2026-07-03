package com.example.qafilah.features.checkout.presentation.summary

import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import java.math.BigDecimal

data class CheckoutSummaryUIState(
    val isRecalculating: Boolean = false,
    val cart: CheckoutCart? = null,
    val selectedDeliveryHandle: String? = null,

    val displaySubtotal: String = "$0.00",
    val displayTax: String = "$0.00",
    val displayShipping: String = "$0.00",
    val displayDiscount: String? = null,
    val displayTotal: String = "$0.00"
)

fun buildSummaryUiState(
    cart: CheckoutCart?,
    isRecalculating: Boolean,
    localSelectedHandle: String?
): CheckoutSummaryUIState {
    if (cart == null) return CheckoutSummaryUIState(isRecalculating = isRecalculating)

    val handleToUse = localSelectedHandle ?: cart.defaultShippingOption?.handle

    val subtotal = cart.cost.subtotalAmount.amount
    val total = cart.cost.totalAmount.amount
    val tax = cart.cost.totalTaxAmount?.amount ?: BigDecimal.ZERO

    val shippingCost = handleToUse?.let { handle ->
        cart.deliveryGroups.firstOrNull()
            ?.deliveryOptions
            ?.find { it.handle == handle }
            ?.estimatedCost?.amount
    } ?: BigDecimal.ZERO

    val discountAmount = (subtotal + tax + shippingCost).subtract(total)

    val displayDiscount = if (discountAmount > BigDecimal.ZERO) {
        "$${discountAmount}"
    } else {
        null
    }

    return CheckoutSummaryUIState(
        isRecalculating = isRecalculating,
        cart = cart,
        selectedDeliveryHandle = handleToUse,
        displaySubtotal = "$${subtotal}",
        displayTax = "$${tax}",
        displayShipping = "$${shippingCost}",
        displayDiscount = displayDiscount,
        displayTotal = "$${total}"
    )
}