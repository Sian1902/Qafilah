package com.example.qafilah.features.orders.presentation.order

data class OrdersUiState(
    val orders: List<OrderUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val labels: OrderLabels? = null
)

data class OrderLabels(
    val orderNumberPrefix: String,
    val datePrefix: String,
    val fulfilledLabel: String,
    val processingLabel: String
)