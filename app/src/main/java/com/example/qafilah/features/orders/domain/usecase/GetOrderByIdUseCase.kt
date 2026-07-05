package com.example.qafilah.features.orders.domain.usecase

import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.repository.OrdersRepository

class GetOrderByIdUseCase(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(accessToken: String, orderId: String): Result<Order?> {
        return repository.getOrderById(accessToken, orderId)
    }
}
