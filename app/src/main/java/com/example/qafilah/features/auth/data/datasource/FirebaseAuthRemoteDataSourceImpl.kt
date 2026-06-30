package com.example.qafilah.features.auth.data.datasource


import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.data.repo.NameUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth
): FirebaseAuthRemoteDataSource {
    override suspend fun signInPrimary(email: String, password: String): AppUser {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("Firebase authentication failed.")

        val (firstName, lastName) = NameUtils.extractNames(user.displayName, user.email)
        return AppUser(id = user.uid, email = email, firstName = firstName, lastName = lastName)
    }

    override suspend fun signUpPrimary(email: String, password: String, fullName: String): AppUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("Registration failed: User instance is null")

        val profileUpdates = userProfileChangeRequest {
            displayName = fullName.trim()
        }
        user.updateProfile(profileUpdates).await()

        val (firstName, lastName) = NameUtils.extractNames(fullName, email)
        return AppUser(id = user.uid, email = email, firstName = firstName, lastName = lastName)
    }

    override fun getCurrentUser(): AppUser? {
        return firebaseAuth.currentUser?.let {
            val (firstName, lastName) = NameUtils.extractNames(it.displayName, it.email)
            AppUser(id = it.uid, email = it.email, firstName = firstName, lastName = lastName)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}