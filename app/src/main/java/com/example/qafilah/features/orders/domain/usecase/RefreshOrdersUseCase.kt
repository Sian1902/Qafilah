package com.example.qafilah.features.orders.domain.usecase

import com.example.qafilah.features.orders.domain.repository.OrdersRepository

class RefreshOrdersUseCase(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(accessToken: String): Result<Unit> {
        return repository.refreshOrders(accessToken)
    }
}
