package com.example.qafilah.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.qafilah.features.wishlist.data.WishlistDao
import com.example.qafilah.features.wishlist.data.WishlistItemEntity

@Database(entities = [WishlistItemEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
}