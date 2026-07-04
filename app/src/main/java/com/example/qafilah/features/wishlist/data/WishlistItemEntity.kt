package com.example.qafilah.features.wishlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "wishlist_items",
    primaryKeys = ["userId", "productId"]
)
data class WishlistItemEntity(
    val userId: String,
    val productId: String,
    val handle: String,
    val title: String,
    val localImagePath: String?,
    val remoteImageUrl: String?,
    val vendor: String?,
    val price: Double,
    val currencyCode: String,
    val addedAt: Long = System.currentTimeMillis()
)
