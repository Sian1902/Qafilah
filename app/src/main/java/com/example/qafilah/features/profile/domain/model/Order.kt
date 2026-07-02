package com.example.qafilah.features.profile.domain.model

data class Order(
    val id: String,
    val orderNumber: Int,
    val processedAt: String,
    val financialStatus: String,
    val fulfillmentStatus: String,
    val totalPrice: Money,
    val lineItems: List<OrderLineItem>
)

data class OrderLineItem(
    val title: String,
    val quantity: Int,
    val price: Money,
    val variantTitle: String?,
    val imageUrl: String?
)

data class Money(
    val amount: String,
    val currencyCode: String
)