package com.example.qafilah.features.wishlist.domain.repository

import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlist(): Flow<List<WishlistItem>>
    suspend fun syncWishlist()
    suspend fun addToWishlist(item: WishlistItem)
    suspend fun removeFromWishlist(productId: String)
    fun isProductWishlisted(productId: String): Flow<Boolean>
    suspend fun clearWishlist()
}