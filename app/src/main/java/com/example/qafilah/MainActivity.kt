package com.example.qafilah

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.qafilah.auth.presentation.AuthState
import com.example.qafilah.auth.presentation.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Monitor authState and print updates to Logcat
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    when (state) {
                        is AuthState.Idle -> {
                            Log.d("FirebaseTest", "State: Idle")
                        }
                        is AuthState.Loading -> {
                            Log.d("FirebaseTest", "State: Loading Registration...")
                        }
                        is AuthState.Success -> {
                            Log.d("FirebaseTest", "State: REGISTRATION SUCCESS! Created User ID: ${state.user.id}")
                        }
                        is AuthState.Error -> {
                            Log.e("FirebaseTest", "State: REGISTRATION FAILED! Error: ${state.message}")
                        }
                    }
                }
            }
        }

        // Trigger the registration testing call with a unique email
        authViewModel.signUp("register_test_user_unique@example.com", "securePassword123")
    }
}