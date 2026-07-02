package com.example.qafilah.features.cart.domain.repo

import com.example.qafilah.features.cart.domain.model.StoreCart
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartState: StateFlow<StoreCart?>

    fun updateLocalCartState(cart: StoreCart?)

    suspend fun fetchCart(): StoreCart?
    suspend fun createCart(variantId: String, quantity: Int)
    suspend fun addItemToCart(variantId: String, quantity: Int)
    suspend fun updateCartItemRemote(lineId: String, quantity: Int)
    suspend fun removeCartItemRemote(lineId: String)
    suspend fun hasActiveCart(): Boolean
}