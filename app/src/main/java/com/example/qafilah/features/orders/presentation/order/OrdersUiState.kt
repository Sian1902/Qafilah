package com.example.qafilah.features.orders.presentation.order

import com.example.qafilah.features.orders.domain.model.Order

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)