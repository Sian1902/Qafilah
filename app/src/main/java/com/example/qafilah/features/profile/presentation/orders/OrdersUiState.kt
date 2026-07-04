package com.example.qafilah.features.profile.presentation.orders

import com.example.qafilah.features.profile.domain.model.Order

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
