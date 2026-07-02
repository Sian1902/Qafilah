package com.example.qafilah.features.auth.domain.usecase


import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.model.AuthError
import com.example.qafilah.features.auth.domain.repository.AuthRepository

class SignInWithGoogleUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(idToken: String): Result<AppUser> {
        return try {
            val user = repository.authenticateWithGoogle(idToken).getOrThrow()
            val email = user.email ?: return Result.failure(Exception("Google account lacks an email address."))

            val secureFederatedPassword = "GoogleOAuth_${user.id}"
            var storefrontTokenResult = repository.authenticateStorefront(email, secureFederatedPassword)

            if (storefrontTokenResult.exceptionOrNull() is AuthError.StorefrontProfileMissing) {
                repository.registerStorefront(
                    email = email,
                    password = secureFederatedPassword,
                    firstName = user.firstName ?: "Valued",
                    lastName = user.lastName ?: "Customer"
                ).getOrThrow()

                storefrontTokenResult = repository.authenticateStorefront(email, secureFederatedPassword)
            }

            val token = storefrontTokenResult.getOrThrow()
            repository.saveSessionToken(token)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}