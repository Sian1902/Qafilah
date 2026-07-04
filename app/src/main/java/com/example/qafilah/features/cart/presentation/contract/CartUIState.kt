package com.example.qafilah.features.cart.presentation.contract

data class CartLineUiModel(
    val id: String,
    val title: String,
    val vendor: String,
    val quantity: Int,
    val displayPrice: String,
    val displayTotal: String,
    val imageUrl: String?,
    val variantTitle: String?
)

data class CartUiModel(
    val totalQuantity: Int,
    val displaySubtotal: String,
    val displayTotal: String,
    val checkoutUrl: String,
    val lines: List<CartLineUiModel>
)

data class CartUIState(
    val isLoading: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val errorMessage: String? = null,
    val cart: CartUiModel? = null
)
