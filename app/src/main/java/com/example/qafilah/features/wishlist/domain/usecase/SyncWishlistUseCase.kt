package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository

class SyncWishlistUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke() = repository.syncWishlist()
}