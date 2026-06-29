package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource

class ClearWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    suspend operator fun invoke() = localDataSource.clearAll()
}