package com.example.qafilah.features.cart.data.datasource

interface CartLocalDataSource{
    suspend fun saveCartId(cartId: String)
    suspend fun getCartId(): String?
    suspend fun deleteCartId()
}