package com.example.qafilah.features.wishlist.domain.model

data class WishlistState(
    val isLoading: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val errorMessage: String? = null,
    val items: List<com.example.ui_kit.components.home.ProductUiModel> = emptyList() // Added entry slot for your existing models[cite: 9]
)