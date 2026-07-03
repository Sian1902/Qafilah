package com.example.qafilah.features.checkout.domain.model

import java.math.BigDecimal

data class CheckoutMoney(
    val amount: BigDecimal,
    val currencyCode: String
) {
    companion object {
        fun from(amount: String, currencyCode: String) =
            CheckoutMoney(BigDecimal(amount), currencyCode)
    }
}

data class CheckoutCost(
    val subtotalAmount: CheckoutMoney,
    val totalAmount: CheckoutMoney,
    val totalTaxAmount: CheckoutMoney?
)

data class DeliveryOption(
    val handle: String,
    val title: String,
    val estimatedCost: CheckoutMoney
)

data class DeliveryGroup(
    val id: String,
    val deliveryOptions: List<DeliveryOption>
)

data class CheckoutLineItem(
    val id: String,
    val productTitle: String,
    val variantTitle: String,
    val quantity: Int,
    val price: CheckoutMoney,
    val imageUrl: String?
)

data class CheckoutCart(
    val id: String,
    val cost: CheckoutCost,
    val deliveryGroups: List<DeliveryGroup>,
    val lines: List<CheckoutLineItem>
) {
    val defaultShippingOption: DeliveryOption?
        get() = deliveryGroups.firstOrNull()?.deliveryOptions?.minByOrNull { it.estimatedCost.amount }
}