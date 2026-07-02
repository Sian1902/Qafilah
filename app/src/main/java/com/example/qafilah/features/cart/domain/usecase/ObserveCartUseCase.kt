package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.repo.CartRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveCartStateUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(): StateFlow<StoreCart?> {
        return repository.cartState
    }
}