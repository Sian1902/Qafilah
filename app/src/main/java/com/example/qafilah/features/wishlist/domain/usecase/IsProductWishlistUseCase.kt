package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IsProductWishlistedUseCase(
    private val localDataSource: WishlistLocalDataSource,
    private val authRepository: AuthRepository
) {
    operator fun invoke(productId: String): Flow<Boolean> {
        val userId = authRepository.getCurrentUser()?.id ?: return flowOf(false)
        return localDataSource.isWishlisted(userId, productId)
    }
}