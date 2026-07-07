package com.example.qafilah.features.wishlist.domain.usecase

import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource

data class AddToWishlistParams(
    val productId: String,
    val handle: String,
    val title: String,
    val imageUrl: String?,
    val vendor: String?,
    val price: Double,
    val currencyCode: String
)

class AddToWishlistUseCase(
    private val localDataSource: WishlistLocalDataSource,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(params: AddToWishlistParams) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        localDataSource.addItem(
            userId = userId,
            productId = params.productId,
            handle = params.handle,
            title = params.title,
            imageUrl = params.imageUrl,
            vendor = params.vendor,
            price = params.price,
            currencyCode = params.currencyCode
        )
    }
}
