package com.example.qafilah.features.cart.data.repo

import com.example.qafilah.features.cart.data.datasource.CartLocalDataSource
import com.example.qafilah.features.cart.data.datasource.CartRemoteDataSource
import com.example.qafilah.features.cart.data.mapper.toDomain
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.repo.CartRepository

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
    private val localDataSource: CartLocalDataSource
): CartRepository {
    override suspend fun getCart(): StoreCart? {
        val cartId = localDataSource.getCartId() ?: return null
        val cart = remoteDataSource.getCart(cartId) ?: return null
        return cart.toDomain()
    }

    override suspend fun updateCartItem(lineId: String, quantity: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun removeCartItem(lineId: String) {
        TODO("Not yet implemented")
    }
}