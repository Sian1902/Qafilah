package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource

class RemoveFromWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    suspend operator fun invoke(productId: String) = localDataSource.removeItem(productId)
}