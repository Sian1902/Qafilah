package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetWishlistUseCase(
    private val localDataSource: WishlistLocalDataSource,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<WishlistItem>> {
        val userId = authRepository.getCurrentUser()?.id ?: return flowOf(emptyList())
        return localDataSource.getWishlist(userId)
    }
}