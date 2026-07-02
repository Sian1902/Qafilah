package com.example.qafilah.features.cart.presentation.contract

sealed interface CartEvent {
    object NavigateToLogin : CartEvent
    object NavigateToSignUp : CartEvent
    object NavigateToHome : CartEvent
}