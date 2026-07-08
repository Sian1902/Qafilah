package com.example.qafilah.features.wishlist.data.remote

import android.system.Os.close
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.tasks.await

class WishlistRemoteDataSourceImpl(
    private val firebaseDatabase: FirebaseDatabase
) : WishlistRemoteDataSource {

    private val wishlistsRef = firebaseDatabase.getReference("wishlists")

    // Helper to sanitize the Shopify ID so Firebase doesn't treat it as a path
    private fun String.toSafeFirebaseKey(): String {
        return this.replace("/", "_").replace(".", "_")
    }

    override fun getWishlist(userId: String): Flow<List<WishlistRemoteDto>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<WishlistRemoteDto>()
                for (child in snapshot.children) {
                    child.getValue(WishlistRemoteDto::class.java)?.let { items.add(it) }
                }
                // Push the updated list down the stream
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors (e.g., permission denied)
                close(error.toException())
            }
        }

        // Attach the realtime listener to the user's wishlist node
        wishlistsRef.child(userId).addValueEventListener(listener)

        // Critical: Remove the listener when the Flow collector/scope is cancelled to prevent memory leaks
        awaitClose {
            wishlistsRef.child(userId).removeEventListener(listener)
        }
    }

    override suspend fun addToWishlist(userId: String, item: WishlistRemoteDto) {
        val safeKey = item.productId.toSafeFirebaseKey()
        // Firebase will now save this at: wishlists/userId/gid:__shopify_Product_12345
        wishlistsRef.child(userId).child(safeKey).setValue(item).await()
    }

    override suspend fun removeFromWishlist(userId: String, productId: String) {
        val safeKey = productId.toSafeFirebaseKey()
        wishlistsRef.child(userId).child(safeKey).removeValue().await()
    }

    override suspend fun clearWishlist(userId: String) {
        wishlistsRef.child(userId).removeValue().await()
    }
}