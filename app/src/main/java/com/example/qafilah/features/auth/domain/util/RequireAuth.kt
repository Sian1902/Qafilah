package com.example.qafilah.features.auth.domain.util

import com.example.qafilah.features.auth.domain.usecase.GetAuthStateUseCase

class RequireAuth(
    private val getAuthStateUseCase: GetAuthStateUseCase
) {

    operator fun invoke(
        onAuthenticated: () -> Unit,
        onGuest: () -> Unit
    ) {
        if (getAuthStateUseCase() != null) {
            onAuthenticated()
        } else {
            onGuest()
        }
    }

    fun isAuthenticated(): Boolean = getAuthStateUseCase() != null
}
