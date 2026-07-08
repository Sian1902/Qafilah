package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IsProductWishlistedUseCase(private val repository: WishlistRepository) {
    operator fun invoke(productId: String) = repository.isProductWishlisted(productId)
}