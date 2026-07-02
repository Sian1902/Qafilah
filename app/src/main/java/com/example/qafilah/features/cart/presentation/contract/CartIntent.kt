package com.example.qafilah.features.cart.presentation.contract

sealed interface CartIntent {
    object EnterScreen : CartIntent
    object NavigateToLogin : CartIntent
    object NavigateToSignUp : CartIntent
    object DismissLoginPrompt : CartIntent

    data class IncreaseQuantity(val lineId: String) : CartIntent
    data class DecreaseQuantity(val lineId: String) : CartIntent
    data class RemoveItem(val lineId: String) : CartIntent
    object DismissError : CartIntent
}