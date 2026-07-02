package com.example.qafilah.features.cart.data.repo

import com.example.qafilah.features.cart.data.datasource.CartLocalDataSource
import com.example.qafilah.features.cart.data.datasource.CartRemoteDataSource
import com.example.qafilah.features.cart.data.mapper.toDomain
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.repo.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
    private val localDataSource: CartLocalDataSource
) : CartRepository {

    private val _cartState = MutableStateFlow<StoreCart?>(null)
    override val cartState = _cartState.asStateFlow()

    override fun updateLocalCartState(cart: StoreCart?) {
        _cartState.value = cart
    }

    override suspend fun fetchCart(): StoreCart? {
        val cartId = localDataSource.getCartId() ?: return null

        val remoteCart = remoteDataSource.getCart(cartId) ?: return null

        val storeCart = remoteCart.cartDetails.toDomain()

        _cartState.value = storeCart
        return storeCart
    }

    override suspend fun createCart(variantId: String, quantity: Int) {
        val remoteCart = remoteDataSource.createCart(variantId, quantity)
            ?: throw Exception("Failed to create cart")

        localDataSource.saveCartId(remoteCart.cartDetails.id)

        val storeCart = remoteCart.cartDetails.toDomain()

        _cartState.value = storeCart
    }

    override suspend fun addItemToCart(variantId: String, quantity: Int) {
        val cartId = localDataSource.getCartId()
            ?: throw Exception("No cart ID found")

        remoteDataSource.addItemToCart(cartId, variantId, quantity)
    }

    override suspend fun updateCartItemRemote(lineId: String, quantity: Int) {
        val cartId = localDataSource.getCartId()
            ?: throw Exception("No cart ID found")

        remoteDataSource.updateCartItem(cartId, lineId, quantity)
    }

    override suspend fun removeCartItemRemote(lineId: String) {
        val cartId = localDataSource.getCartId()
            ?: throw Exception("No cart ID found")

        remoteDataSource.removeCartItem(cartId, lineId)
    }
}