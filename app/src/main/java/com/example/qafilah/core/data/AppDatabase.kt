package com.example.qafilah.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.qafilah.features.assistant.data.local.AssistantDao
import com.example.qafilah.features.assistant.data.local.AssistantMessageEntity
import com.example.qafilah.features.wishlist.data.WishlistDao
import com.example.qafilah.features.wishlist.data.WishlistItemEntity
import com.example.qafilah.features.orders.data.OrderDao
import com.example.qafilah.features.orders.data.OrderEntity
import com.example.qafilah.features.orders.data.OrderLineItemEntity

@Database(entities = [WishlistItemEntity::class,
    AssistantMessageEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun assistantDao(): AssistantDao

    @Database(
        entities = [WishlistItemEntity::class, OrderEntity::class, OrderLineItemEntity::class],
        version = 5,
        exportSchema = false
    )
    abstract class AppDatabase : RoomDatabase() {
        abstract fun wishlistDao(): WishlistDao
        abstract fun orderDao(): OrderDao
    }
}