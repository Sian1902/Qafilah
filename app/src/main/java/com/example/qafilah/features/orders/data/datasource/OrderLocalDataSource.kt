package com.example.qafilah.features.orders.data.datasource

import com.example.qafilah.features.orders.data.OrderDao
import com.example.qafilah.features.orders.data.OrderEntity
import com.example.qafilah.features.orders.data.OrderLineItemEntity
import com.example.qafilah.features.orders.data.OrderWithLineItems
import kotlinx.coroutines.flow.Flow

class OrderLocalDataSource(private val orderDao: OrderDao) {
    fun getOrders(): Flow<List<OrderWithLineItems>> = orderDao.getOrdersWithLineItems()
    
    suspend fun getOrderById(orderId: String): OrderWithLineItems? = orderDao.getOrderById(orderId)

    suspend fun saveOrders(orders: List<OrderEntity>, lineItems: List<OrderLineItemEntity>) {
        orderDao.replaceOrders(orders, lineItems)
    }

    suspend fun clearCache() {
        orderDao.clearOrders()
        orderDao.clearLineItems()
    }
}
