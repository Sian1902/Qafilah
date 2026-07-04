package com.example.qafilah.features.cart.presentation.contract

sealed interface CartIntent {
    data class EnterScreen(val fallbackErrorMessage: String) : CartIntent
    object NavigateToLogin : CartIntent
    object NavigateToSignUp : CartIntent
    object DismissLoginPrompt : CartIntent

    data class IncreaseQuantity(val lineId: String) : CartIntent
    data class DecreaseQuantity(val lineId: String) : CartIntent
    data class RemoveItem(val lineId: String) : CartIntent
    object DismissError : CartIntent

    data class UpdateDiscountInput(val code: String) : CartIntent
    object ApplyDiscountCode : CartIntent
    data class RemoveDiscountCode(val code: String) : CartIntent
}