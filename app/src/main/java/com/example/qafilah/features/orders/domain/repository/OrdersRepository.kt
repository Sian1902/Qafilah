package com.example.qafilah.features.orders.domain.repository

import com.example.qafilah.features.orders.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    fun getOrders(): Flow<List<Order>>
    suspend fun refreshOrders(accessToken: String): Result<Unit>
    suspend fun getOrderById(orderId: String): Order?
}
