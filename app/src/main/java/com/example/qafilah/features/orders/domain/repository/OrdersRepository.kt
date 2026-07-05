package com.example.qafilah.features.orders.domain.repository

import com.example.qafilah.features.orders.domain.model.Order

import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    suspend fun getOrders(accessToken: String): Result<List<Order>>
    suspend fun getOrderById(accessToken: String, orderId: String): Result<Order?>
}
