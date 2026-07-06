package com.example.qafilah.features.orders.presentation.order_details

data class OrderDetailsUiState(
    val order: OrderDetailUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class OrderDetailLabels(
    val orderNumberPrefix: String,
    val datePrefix: String,
    val fulfilledLabel: String,
    val processingLabel: String,
    val quantityFormat: String
)