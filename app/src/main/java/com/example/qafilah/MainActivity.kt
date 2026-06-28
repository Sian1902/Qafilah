package com.example.qafilah

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.qafilah.auth.presentation.AuthViewModel
import com.example.qafilah.auth.presentation.screens.LoginScreen
import com.example.qafilah.screens.splash.SplashScreen
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QafilahTheme {
//                SplashScreen(
//                    onSplashFinished = {}
//                )
                LoginScreen(
                    modifier = Modifier,
                    onNavigateToSignUp = {},
                    onNavigateToHome = {  }
                )
            }
        }
    }
}

