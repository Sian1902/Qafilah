package com.example.qafilah

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.qafilah.auth.presentation.AuthState
import com.example.qafilah.auth.presentation.AuthViewModel
import com.example.qafilah.ui.theme.QafilahTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    // 1. Koin injects the ViewModel here
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Listen to the AuthState
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    when (state) {
                        is AuthState.Idle -> Log.d("FirebaseTest", "State: Idle")
                        is AuthState.Loading -> Log.d("FirebaseTest", "State: Loading...")
                        is AuthState.Success -> {
                            Log.d("FirebaseTest", "State: SUCCESS! User ID: ${state.user.id}")
                        }
                        is AuthState.Error -> {
                            Log.e("FirebaseTest", "State: FAILED! Error: ${state.message}")
                        }
                    }
                }
            }
        }

        // 3. Trigger the sign-in with your test credentials
        authViewModel.signIn("test@example.com", "123456")
    }
}