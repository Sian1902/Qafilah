package com.example.qafilah.features.wishlist.data.repository

import com.example.qafilah.features.auth.data.datasource.FirebaseAuthRemoteDataSource
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.data.remote.WishlistRemoteDataSource
import com.example.qafilah.features.wishlist.data.remote.WishlistRemoteDto
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WishlistRepositoryImpl(
    private val localDataSource: WishlistLocalDataSource,
    private val remoteDataSource: WishlistRemoteDataSource,
    private val authDataSource: FirebaseAuthRemoteDataSource
) : WishlistRepository {

    private val userId: String
        get() = authDataSource.getCurrentUser()?.id ?: throw Exception("User not authenticated")

    override fun getWishlist(): Flow<List<WishlistItem>> {
        // FIX 1: localDataSource already returns Flow<List<WishlistItem>>, so we remove the redundant mapping.
        return localDataSource.getWishlist(userId)
    }

    override suspend fun syncWishlist() {
        try {
            val uid = userId

            // 1. Continuous collection from the real-time Firebase stream
            remoteDataSource.getWishlist(uid).collect { remoteItems ->

                // Get a quick snapshot list of what is currently in Room locally
                // Note: assuming your localDataSource has a one-shot fetch or we take the first emission
                // If getWishlist returns a Flow, use .first() to read its current state once
                val localItemsSnapshot = localDataSource.getWishlist(uid).first()

                // 2. Handle Deletions: If an item is local but NOT in remote data, delete it locally
                val remoteProductIds = remoteItems.map { it.productId }.toSet()
                localItemsSnapshot.forEach { localItem ->
                    if (localItem.productId !in remoteProductIds) {
                        localDataSource.removeItem(uid, localItem.productId)
                    }
                }

                // 3. Handle Additions/Updates: Insert or update remote items into the local DB
                remoteItems.forEach { remoteItem ->
                    localDataSource.addItem(
                        userId = uid,
                        productId = remoteItem.productId,
                        handle = remoteItem.handle,
                        title = remoteItem.title,
                        imageUrl = remoteItem.remoteImageUrl,
                        vendor = remoteItem.vendor,
                        price = remoteItem.price,
                        currencyCode = remoteItem.currencyCode
                    )
                }
            }
        } catch (e: Exception) {
            // Fails silently if offline; local DB acts as fallback
        }
    }

    override suspend fun addToWishlist(item: WishlistItem) {
        val uid = userId

        // FIX 3: Use addItem and pass the individual parameters expected by LocalDataSource
        localDataSource.addItem(
            userId = uid,
            productId = item.productId,
            handle = item.handle,
            title = item.title,
            imageUrl = item.remoteImageUrl,
            vendor = item.vendor,
            price = item.price,
            currencyCode = item.currencyCode
        )

        remoteDataSource.addToWishlist(uid, item.toRemoteDto()) // Background remote push
    }

    override suspend fun removeFromWishlist(productId: String) {
        val uid = userId
        // FIX 4: Call removeItem instead of removeFromWishlist
        localDataSource.removeItem(uid, productId)
        remoteDataSource.removeFromWishlist(uid, productId)
    }

    override fun isProductWishlisted(productId: String): Flow<Boolean> {
        // FIX 5: Call isWishlisted instead of isProductWishlisted
        return localDataSource.isWishlisted(userId, productId)
    }

    override suspend fun clearWishlist() {
        val uid = userId
        // FIX 6: Call clearAll instead of clearWishlist
        localDataSource.clearAll(uid)
        remoteDataSource.clearWishlist(uid)
    }

    // --- Mappers ---

    // We removed the Entity.toDomain mapper because LocalDataSource handles it for us now.

    private fun WishlistItem.toRemoteDto() = WishlistRemoteDto(
        productId = productId, handle = handle, title = title,
        localImagePath = localImagePath, remoteImageUrl = remoteImageUrl,
        vendor = vendor, price = price, currencyCode = currencyCode, addedAt = addedAt
    )
}