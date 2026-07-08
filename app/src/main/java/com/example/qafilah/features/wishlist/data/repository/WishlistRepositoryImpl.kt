package com.example.qafilah.features.wishlist.data.repository

import com.example.qafilah.features.auth.data.datasource.FirebaseAuthRemoteDataSource
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.data.remote.WishlistRemoteDataSource
import com.example.qafilah.features.wishlist.data.remote.WishlistRemoteDto
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class WishlistRepositoryImpl(
    private val localDataSource: WishlistLocalDataSource,
    private val remoteDataSource: WishlistRemoteDataSource,
    private val authDataSource: FirebaseAuthRemoteDataSource
) : WishlistRepository {

    private val userId: String
        get() = authDataSource.getCurrentUser()?.id ?: throw Exception("User not authenticated")

    override fun getWishlist(): Flow<List<WishlistItem>> {
        val uid = authDataSource.getCurrentUser()?.id ?: return flowOf(emptyList())
        return localDataSource.getWishlist(uid)
    }

    override suspend fun syncWishlist() {
        val uid = authDataSource.getCurrentUser()?.id ?: return
        try {
            remoteDataSource.getWishlist(uid).collect { remoteItems ->

                val localItemsSnapshot = localDataSource.getWishlist(uid).first()

                val remoteProductIds = remoteItems.map { it.productId }.toSet()
                localItemsSnapshot.forEach { localItem ->
                    if (localItem.productId !in remoteProductIds) {
                        localDataSource.removeItem(uid, localItem.productId)
                    }
                }

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
        }
    }

    override suspend fun addToWishlist(item: WishlistItem) {
        val uid = userId

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

        remoteDataSource.addToWishlist(uid, item.toRemoteDto())
    }

    override suspend fun removeFromWishlist(productId: String) {
        val uid = userId
        localDataSource.removeItem(uid, productId)
        remoteDataSource.removeFromWishlist(uid, productId)
    }

    override fun isProductWishlisted(productId: String): Flow<Boolean> {
        val uid = authDataSource.getCurrentUser()?.id ?: return flowOf(false)
        return localDataSource.isWishlisted(uid, productId)
    }

    override suspend fun clearWishlist() {
        val uid = userId
        localDataSource.clearAll(uid)
        remoteDataSource.clearWishlist(uid)
    }


    private fun WishlistItem.toRemoteDto() = WishlistRemoteDto(
        productId = productId, handle = handle, title = title,
        localImagePath = localImagePath, remoteImageUrl = remoteImageUrl,
        vendor = vendor, price = price, currencyCode = currencyCode, addedAt = addedAt
    )
}