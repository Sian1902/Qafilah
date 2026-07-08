package com.example.qafilah.features.wishlist.data.remote

import kotlinx.coroutines.flow.Flow

interface WishlistRemoteDataSource {
    fun getWishlist(userId: String): Flow<List<WishlistRemoteDto>>
    suspend fun addToWishlist(userId: String, item: WishlistRemoteDto)
    suspend fun removeFromWishlist(userId: String, productId: String)
    suspend fun clearWishlist(userId: String)
}