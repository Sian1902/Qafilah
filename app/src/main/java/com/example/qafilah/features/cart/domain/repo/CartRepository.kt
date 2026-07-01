package com.example.qafilah.features.cart.domain.repo

import com.example.qafilah.features.cart.domain.model.StoreCart

interface CartRepository {
    suspend fun getCart(): StoreCart?
    suspend fun updateCartItem(lineId: String, quantity: Int)
    suspend fun removeCartItem(lineId: String)
}