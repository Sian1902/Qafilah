package com.example.qafilah.features.auth.data.repo

import com.apollographql.apollo.api.Optional
import com.example.qafilah.core.token.TokenLocalDataSource
import com.example.qafilah.features.auth.data.datasource.FirebaseAuthRemoteDataSource
import com.example.qafilah.features.auth.data.datasource.ShopifyAuthRemoteDataSource
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.model.AuthError
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.graphql.storefront.type.CustomerAccessTokenCreateInput
import com.example.qafilah.graphql.storefront.type.CustomerCreateInput

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseDataSource: FirebaseAuthRemoteDataSource,
    private val shopifyDataSource: ShopifyAuthRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
): AuthRepository {
    override suspend fun authenticateWithGoogle(idToken: String): Result<AppUser> {
        return runCatching {
            firebaseDataSource.signInWithGoogle(idToken)
        }
    }

    override suspend fun authenticatePrimary(email: String, password: String): Result<AppUser> = runCatching {
        firebaseDataSource.signInPrimary(email, password)
    }

    override suspend fun registerPrimary(email: String, password: String, fullName: String): Result<AppUser> = runCatching {
        firebaseDataSource.signUpPrimary(email, password, fullName)
    }

    override suspend fun authenticateStorefront(email: String, password: String): Result<String> = runCatching {
        val loginInput = CustomerAccessTokenCreateInput(email = email, password = password)
        val loginResult = shopifyDataSource.loginCustomer(loginInput)

        val isUnrecognized = loginResult?.customerUserErrors?.any { it.code.toString() == "UNIDENTIFIED_CUSTOMER" } == true
        if (isUnrecognized) throw AuthError.StorefrontProfileMissing

        loginResult?.customerAccessToken?.accessToken ?: throw Exception("Failed to acquire token.")
    }

    override suspend fun registerStorefront(email: String, password: String, firstName: String?, lastName: String?)
    : Result<Unit> = runCatching {
        val registerInput = CustomerCreateInput(
            email = email,
            password = password,
            firstName = Optional.presentIfNotNull(firstName),
            lastName = Optional.presentIfNotNull(lastName)
        )
        shopifyDataSource.registerCustomer(registerInput)
    }

    override suspend fun saveSessionToken(token: String) {
        tokenLocalDataSource.saveToken(token)
    }

    override fun getCurrentUser(): AppUser? = firebaseDataSource.getCurrentUser()

    override suspend fun signOut() {
        firebaseDataSource.signOut()
        tokenLocalDataSource.clearToken()
    }
}