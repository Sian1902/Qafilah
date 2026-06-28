package com.example.qafilah.wishlist.domain.usecase

import com.example.qafilah.wishlist.data.WishlistLocalDataSource

class RemoveFromWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    suspend operator fun invoke(productId: String) = localDataSource.removeItem(productId)
}