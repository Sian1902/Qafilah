package com.example.qafilah.features.orders.data.repo

import com.example.qafilah.features.orders.data.datasource.OrderLocalDataSource
import com.example.qafilah.features.orders.data.mapper.toDomain
import com.example.qafilah.features.orders.data.mapper.toEntity
import com.example.qafilah.features.orders.data.mapper.toOrdersDomain
import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.repository.OrdersRepository
import com.example.qafilah.features.profile.data.datasource.ProfileRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrdersRepositoryImpl(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: OrderLocalDataSource
) : OrdersRepository {

    override fun getOrders(): Flow<List<Order>> {
        return localDataSource.getOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshOrders(accessToken: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val customer = profileRemoteDataSource.getCustomerProfile(accessToken)
                    ?: return@withContext Result.failure(Exception("Customer not found"))

                val orders = customer.toOrdersDomain()
                
                val orderEntities = orders.map { it.toEntity() }
                val lineItemEntities = orders.flatMap { order ->
                    order.lineItems.map { it.toEntity(order.id) }
                }


                localDataSource.clearCache()
                localDataSource.saveOrders(orderEntities, lineItemEntities)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getOrderById(orderId: String): Order? {
        return localDataSource.getOrderById(orderId)?.toDomain()
    }
}
