package com.example.qafilah.features.auth.presentation

import android.util.Log // Make sure to import this
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenLocalDataSource
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.usecase.SignInUseCase
import com.example.qafilah.features.auth.domain.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val tokenLocalDataSource: TokenLocalDataSource // Temporarily injected for debugging
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = signInUseCase(email, password)

            result.fold(
                onSuccess = { user ->
                    // --- TEMPORARY LOGGING ---
                    val token = tokenLocalDataSource.getToken() ?: ""
                    Log.e("AuthDebug", "✅ SIGN IN SUCCESS")
                    Log.e("AuthDebug", "User Email: ${user.email}")
                    Log.e("AuthDebug", "Firebase UID: ${user.id}")
                    Log.e("AuthDebug", "First Name: ${user.firstName} | Last Name: ${user.lastName}")
                    Log.e("AuthDebug", "Shopify Token: ${token}")
                    // -------------------------

                    _authState.value = AuthState.Success(user)
                },
                onFailure = { error ->
                    Log.e("AuthDebug", "❌ SIGN IN FAILED: ${error.message}")
                    _authState.value = AuthState.Error(error.message ?: "Login failed")
                }
            )
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = signUpUseCase(email, password)

            result.fold(
                onSuccess = { user ->
                    // --- TEMPORARY LOGGING ---

                    val token = tokenLocalDataSource.getToken() ?: ""
                    Log.e("AuthDebug", "✅ SIGN UP SUCCESS")
                    Log.e("AuthDebug", "User Email: ${user.email}")
                    Log.e("AuthDebug", "Firebase UID: ${user.id}")
                    Log.e("AuthDebug", "Shopify Token: ${token}")
                    // -------------------------

                    _authState.value = AuthState.Success(user)
                },
                onFailure = { error ->
                    Log.e("AuthDebug", "❌ SIGN UP FAILED: ${error.message}")
                    _authState.value = AuthState.Error(error.message ?: "Registration failed")
                }
            )
        }
    }
}
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: AppUser) : AuthState()
    data class Error(val message: String) : AuthState()
}