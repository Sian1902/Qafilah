package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository

data class AddToWishlistParams(
    val productId: String,
    val handle: String,
    val title: String,
    val imageUrl: String?,
    val vendor: String?,
    val price: Double,
    val currencyCode: String
)

class AddToWishlistUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(item: WishlistItem) = repository.addToWishlist(item)
}
