package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource

class AddToWishlistUseCase(
    private val localDataSource: WishlistLocalDataSource,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        productId: String, handle: String, title: String,
        imageUrl: String?, vendor: String?, price: Double, currencyCode: String
    ) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        localDataSource.addItem(userId, productId, handle, title, imageUrl, vendor, price, currencyCode)
    }
}