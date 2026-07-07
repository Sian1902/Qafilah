package com.example.qafilah.features.orders.presentation.order

data class OrderUiModel(
    val id: String,
    val orderNumber: String,
    val date: String,
    val totalPrice: String,
    val status: String,
    val statusLabel: String
)
