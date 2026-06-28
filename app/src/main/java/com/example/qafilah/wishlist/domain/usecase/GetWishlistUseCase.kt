package com.example.qafilah.wishlist.domain.usecase

import com.example.qafilah.wishlist.data.WishlistLocalDataSource
import com.example.qafilah.wishlist.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow

class GetWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    operator fun invoke(productId: String): Flow<List<WishlistItem>> = localDataSource.getWishlist()
}