package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetWishlistUseCase(private val repository: WishlistRepository) {
    operator fun invoke() = repository.getWishlist()
}