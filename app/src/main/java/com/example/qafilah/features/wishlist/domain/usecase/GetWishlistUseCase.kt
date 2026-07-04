package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow

class GetWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    operator fun invoke(): Flow<List<WishlistItem>> = localDataSource.getWishlist()
}