package com.example.qafilah.features.auth.domain.model

sealed class AuthError(override val message: String) : Exception(message) {
    object StorefrontProfileMissing : AuthError("Shopify profile not found.")
    data class Network(override val message: String) : AuthError(message)
    data class Unknown(override val message: String) : AuthError(message)
}