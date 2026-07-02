package com.example.qafilah.features.auth.data.datasource


import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.data.repo.NameUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    override suspend fun signUpPrimary(name: String ,email: String, password: String): AppUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("Registration failed: User instance is null")

        val profileUpdates = userProfileChangeRequest {
            displayName = name.trim()
        }
        user.updateProfile(profileUpdates).await()

        val (firstName, lastName) = NameUtils.extractNames(name, email)
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

    override suspend fun signInWithGoogle(idToken: String): AppUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val firebaseUser = authResult.user ?: throw Exception("Firebase Google sign-in failed: User is null.")

        val nameParts = firebaseUser.displayName?.split(" ", limit = 2)
        val firstName = nameParts?.getOrNull(0)
        val lastName = nameParts?.getOrNull(1)

        return AppUser(
            id = firebaseUser.uid,
            email = firebaseUser.email,
            firstName = firstName,
            lastName = lastName
        )
    }
}