package com.example.qafilah.features.auth.domain.model

data class AppUser(
    val id: String,
    val email: String?,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { email ?: "Guest" }
}
