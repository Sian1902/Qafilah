package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository

class RemoveFromWishlistUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(productId: String) = repository.removeFromWishlist(productId)
}