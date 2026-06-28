package com.example.qafilah.wishlist.domain.model

data class WishlistItem(
    val productId: String,
    val handle: String,
    val title: String,
    val localImagePath: String?,
    val remoteImageUrl: String?,
    val vendor: String?,
    val price: Double,
    val currencyCode: String,
    val addedAt: Long
)
