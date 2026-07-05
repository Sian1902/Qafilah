package com.example.qafilah.features.orders.domain.usecase

import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.repository.OrdersRepository

class GetOrdersUseCase(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(accessToken: String): Result<List<Order>> {
        return repository.getOrders(accessToken)
    }
}
