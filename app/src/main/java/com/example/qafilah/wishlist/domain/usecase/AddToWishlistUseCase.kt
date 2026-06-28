package com.example.qafilah.wishlist.domain.usecase

import com.example.qafilah.wishlist.data.WishlistLocalDataSource

class AddToWishlistUseCase(private val localDataSource: WishlistLocalDataSource) {
    suspend operator fun invoke(
        productId: String, handle: String, title: String,
        imageUrl: String?, vendor: String?, price: Double, currencyCode: String
    ) = localDataSource.addItem(productId, handle, title, imageUrl, vendor, price, currencyCode)
}