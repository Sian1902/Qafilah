package com.example.qafilah.features.orders.domain.usecase

import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase(
    private val repository: OrdersRepository
) {
    operator fun invoke(): Flow<List<Order>> {
        return repository.getOrders()
    }
}
