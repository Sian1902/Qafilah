package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource

class RemoveFromWishlistUseCase(
    private val localDataSource: WishlistLocalDataSource,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(productId: String) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        localDataSource.removeItem(userId, productId)
    }
}