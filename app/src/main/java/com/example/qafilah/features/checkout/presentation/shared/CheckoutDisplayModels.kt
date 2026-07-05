package com.example.qafilah.features.checkout.presentation.shared

data class CheckoutDisplayLineItem(
    val id: String,
    val title: String,
    val variantTitle: String,
    val quantity: Int,
    val displayPrice: String,
    val displayTotal: String,
    val imageUrl: String?
)

data class CheckoutDisplayDeliveryOption(
    val handle: String,
    val title: String,
    val displayCost: String
)

data class CheckoutDisplayCart(
    val id: String,
    val displaySubtotal: String,
    val displayTax: String,
    val displayShipping: String,
    val displayDiscount: String?,
    val displayTotal: String,
    val lines: List<CheckoutDisplayLineItem>,
    val deliveryOptions: List<CheckoutDisplayDeliveryOption>
)
