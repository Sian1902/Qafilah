package com.example.qafilah.features.auth.data
// feature/data/repository/AuthRepositoryImpl.kt
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AppUser> {
        return try {
            // .await() suspends until the Firebase Task completes
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                Result.success(AppUser(id = user.uid, email = user.email))
            } else {
                Result.failure(Exception("Unknown error occurred"))
            }
        } catch (e: Exception) {
            // Catches FirebaseAuthInvalidCredentialsException, etc.
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): AppUser? {
        return firebaseAuth.currentUser?.let {
            AppUser(id = it.uid, email = it.email)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
    override suspend fun signUp(email: String, password: String): Result<AppUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                Result.success(AppUser(id = user.uid, email = user.email))
            } else {
                Result.failure(Exception("Registration failed: User instance is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}