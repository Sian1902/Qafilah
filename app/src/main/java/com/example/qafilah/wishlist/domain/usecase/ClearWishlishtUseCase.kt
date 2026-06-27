package com.example.qafilah.wishlist.domain.usecase

import com.example.qafilah.wishlist.data.WishlistLocalDataSource

class ClearWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    suspend operator fun invoke() = localDataSource.clearAll()
}