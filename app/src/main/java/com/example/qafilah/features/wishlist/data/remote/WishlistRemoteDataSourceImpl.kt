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
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        wishlistsRef.child(userId).addValueEventListener(listener)

        awaitClose {
            wishlistsRef.child(userId).removeEventListener(listener)
        }
    }

    override suspend fun addToWishlist(userId: String, item: WishlistRemoteDto) {
        val safeKey = item.productId.toSafeFirebaseKey()
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