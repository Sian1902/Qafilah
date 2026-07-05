package com.example.qafilah.features.orders.data.repo

import com.example.qafilah.features.orders.data.mapper.toOrdersDomain
import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.repository.OrdersRepository
import com.example.qafilah.features.profile.data.datasource.ProfileRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrdersRepositoryImpl(
    private val profileRemoteDataSource: ProfileRemoteDataSource
) : OrdersRepository {
    override suspend fun getOrders(accessToken: String): Result<List<Order>> =
        withContext(Dispatchers.IO) {
            try {
                val customer = profileRemoteDataSource.getCustomerProfile(accessToken)
                    ?: return@withContext Result.failure(Exception("Customer not found"))

                Result.success(customer.toOrdersDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getOrderById(accessToken: String, orderId: String): Result<Order?> {
        return getOrders(accessToken).map { orders ->
            orders.find { it.id == orderId }
        }
    }
}
