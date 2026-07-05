package com.example.qafilah.features.wishlist.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qafilah.features.wishlist.data.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getWishlist(userId: String): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    suspend fun remove(userId: String, productId: String)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId")
    suspend fun clearAll(userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE userId = :userId AND productId = :productId)")
    fun isWishlisted(userId: String, productId: String): Flow<Boolean>

    @Query("SELECT localImagePath FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    suspend fun getLocalImagePath(userId: String, productId: String): String?
}