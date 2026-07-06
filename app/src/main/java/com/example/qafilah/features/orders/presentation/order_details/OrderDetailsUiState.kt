package com.example.qafilah.features.orders.presentation.order_details

import com.example.qafilah.features.orders.domain.model.Order

data class OrderDetailsUiState(
    val order: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)