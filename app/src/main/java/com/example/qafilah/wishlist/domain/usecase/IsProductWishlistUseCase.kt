package com.example.qafilah.wishlist.domain.usecase

import com.example.qafilah.wishlist.data.WishlistLocalDataSource
import kotlinx.coroutines.flow.Flow

class IsProductWishlistedUseCase(private val localDataSource: WishlistLocalDataSource) {
    operator fun invoke(productId: String): Flow<Boolean> = localDataSource.isWishlisted(productId)
}