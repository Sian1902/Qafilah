package com.example.qafilah.features.wishlist.data.remote

data class WishlistRemoteDto(
    val productId: String = "",
    val handle: String = "",
    val title: String = "",
    val localImagePath: String? = null,
    val remoteImageUrl: String? = null,
    val vendor: String? = null,
    val price: Double = 0.0,
    val currencyCode: String = "",
    val addedAt: Long = 0L
)