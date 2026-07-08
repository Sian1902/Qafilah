package com.example.qafilah.features.wishlist.data.local

import android.content.Context
import com.example.qafilah.features.wishlist.domain.model.WishlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class WishlistLocalDataSource(
    private val dao: WishlistDao,
    private val context: Context,
    private val httpClient: OkHttpClient
) {

    fun getWishlist(userId: String): Flow<List<WishlistItem>> =
        dao.getWishlist(userId).map { entities -> entities.map { it.toDomain() } }

    fun isWishlisted(userId: String, productId: String): Flow<Boolean> =
        dao.isWishlisted(userId, productId)

    suspend fun addItem(
        userId: String,
        productId: String,
        handle: String,
        title: String,
        imageUrl: String?,
        vendor: String?,
        price: Double,
        currencyCode: String
    ) {
        val localPath = imageUrl?.let { cacheImageLocally(it, productId) }
        dao.add(
            WishlistItemEntity(
                userId = userId,
                productId = productId,
                handle = handle,
                title = title,
                localImagePath = localPath,
                remoteImageUrl = imageUrl,
                vendor = vendor,
                price = price,
                currencyCode = currencyCode
            )
        )
    }

    suspend fun removeItem(userId: String, productId: String) {
        val localPath = dao.getLocalImagePath(userId, productId)
        dao.remove(userId, productId)
        localPath?.let { File(it).delete() }
    }

    suspend fun clearAll(userId: String) {
        dao.clearAll(userId)
    }

    private suspend fun cacheImageLocally(imageUrl: String, productId: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val outFile = File(context.filesDir, "wishlist_images/${productId.hashCode()}.jpg")
                outFile.parentFile?.mkdirs()
                val request = Request.Builder().url(imageUrl).build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    response.body?.byteStream()?.use { input ->
                        outFile.outputStream().use { output -> input.copyTo(output) }
                    }
                }
                outFile.absolutePath
            } catch (e: Exception) {
                null
            }
        }

    private fun WishlistItemEntity.toDomain() = WishlistItem(
        productId = productId, handle = handle, title = title,
        localImagePath = localImagePath, remoteImageUrl = remoteImageUrl,
        vendor = vendor, price = price, currencyCode = currencyCode, addedAt = addedAt
    )
}