package com.example.qafilah.features.orders.presentation.order_details

data class OrderDetailUiModel(
    val id: String,
    val orderNumber: String,
    val date: String,
    val status: String,
    val statusLabel: String,
    val totalPrice: String,
    val lineItems: List<OrderLineItemUiModel>
)

data class OrderLineItemUiModel(
    val title: String,
    val quantity: String,
    val price: String,
    val variantTitle: String?,
    val imageUrl: String?
)
