package com.example.qafilah.features.auth.data.repo

import com.apollographql.apollo.api.Optional
import com.example.qafilah.core.token.TokenLocalDataSource
import com.example.qafilah.features.auth.data.datasource.ShopifyAuthRemoteDataSource
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.graphql.storefront.type.CustomerAccessTokenCreateInput
import com.example.qafilah.graphql.storefront.type.CustomerCreateInput

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val shopifyRemoteDataSource: ShopifyAuthRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
): AuthRepository {


    override suspend fun signIn(email: String, password: String): Result<AppUser> {
        return try {
            // 1. Authenticate primary identity with Firebase
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Firebase authentication failed."))

            // 2. Attempt Shopify Login
            val loginInput = CustomerAccessTokenCreateInput(email = email, password = password)
            var loginResult = shopifyRemoteDataSource.loginCustomer(loginInput)

            // 3. The Self-Healing Mechanism
            // Check if Shopify threw an UNIDENTIFIED_CUSTOMER error (meaning Firebase exists, but Shopify doesn't)
            val errors = loginResult?.customerUserErrors
            val isUnrecognized = errors?.any { it.code.toString() == "UNIDENTIFIED_CUSTOMER" } == true || loginResult?.customerAccessToken == null

            if (isUnrecognized) {
                // Safely extract names from the Firebase profile, or fallback to parsing the email
                val (firstName, lastName) = NameUtils.extractNames(firebaseUser.displayName, email, defaultFirst = "Valued", defaultLast = "Customer")

                // Silently register the missing Shopify account
                val registerInput = CustomerCreateInput(
                    email = email,
                    password = password,
                    firstName = Optional.present(firstName),
                    lastName = Optional.present(lastName)
                )
                shopifyRemoteDataSource.registerCustomer(registerInput)

                // Retry the Shopify login to grab the token
                loginResult = shopifyRemoteDataSource.loginCustomer(loginInput)
            }

            // 4. Secure the session access token locally via Tink + DataStore
            val accessToken = loginResult?.customerAccessToken?.accessToken
            if (accessToken != null) {
                tokenLocalDataSource.saveToken(accessToken)

                // Return the unified domain model
                // Derive display name safely (fallback to email local-part when displayName is absent)
                val (firstName, lastName) = NameUtils.extractNames(firebaseUser.displayName, firebaseUser.email)

                Result.success(
                    AppUser(
                        id = firebaseUser.uid,
                        email = firebaseUser.email,
                        firstName = firstName,
                        lastName = lastName
                    )
                )
            } else {
                Result.failure(Exception("Failed to acquire storefront authorization token."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<AppUser> {
        return try {
            // 1. Create the base Firebase account
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Registration failed: User instance is null"))

            // 2. Build and execute mirroring Shopify account (Names are optional here)

            val (firstName, lastName) = NameUtils.extractNames(firebaseUser.displayName, email, defaultFirst = "Valued", defaultLast = "Customer")

            val registerInput = CustomerCreateInput(
                email = email,
                password = password,
                firstName = Optional.present(firstName),
                lastName = Optional.present(lastName)
            )
            shopifyRemoteDataSource.registerCustomer(registerInput)

            // 3. Log in to get the storefront access token immediately
            val loginInput = CustomerAccessTokenCreateInput(email = email, password = password)
            val loginResult = shopifyRemoteDataSource.loginCustomer(loginInput)
            val accessToken = loginResult?.customerAccessToken?.accessToken

            if (accessToken != null) {
                tokenLocalDataSource.saveToken(accessToken)
                // Return AppUser with the names we already derived above so callers don't get nulls
                Result.success(
                    AppUser(
                        id = firebaseUser.uid,
                        email = firebaseUser.email,
                        firstName = firstName,
                        lastName = lastName
                    )
                )
            } else {
                // If this edge case hits, the self-healing signIn flow will fix it later
                Result.failure(Exception("Profile created successfully, but session initialization timed out. Please sign in."))
            }

        } catch (e: Exception) {
            // If Firebase succeeds but Shopify totally crashes, we return the error.
            // The user will be prompted to log in, triggering the self-healing flow.
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): AppUser? {
        return firebaseAuth.currentUser?.let {
            val (firstName, lastName) = NameUtils.extractNames(it.displayName, it.email)
            AppUser(
                id = it.uid,
                email = it.email,
                firstName = firstName,
                lastName = lastName
            )
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()

        tokenLocalDataSource.clearToken()
    }

}