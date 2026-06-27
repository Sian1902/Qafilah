package com.example.qafilah.core.data

import WishlistDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.qafilah.wishlist.data.WishlistItemEntity

@Database(entities = [WishlistItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
}