package com.example.qafilah.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.qafilah.features.orders.data.OrderDao
import com.example.qafilah.features.orders.data.OrderEntity
import com.example.qafilah.features.orders.data.OrderLineItemEntity
import com.example.qafilah.features.wishlist.data.WishlistDao
import com.example.qafilah.features.wishlist.data.WishlistItemEntity

@Database(
    entities = [WishlistItemEntity::class, OrderEntity::class, OrderLineItemEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
}