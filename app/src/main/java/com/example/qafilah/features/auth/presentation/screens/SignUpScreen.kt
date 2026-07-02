package com.example.qafilah.features.auth.presentation.screens

import android.util.Log
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import android.app.Activity
import android.content.ContextWrapper
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.R
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.presentation.AuthState
import com.example.qafilah.features.auth.presentation.AuthViewModel
import com.example.ui_kit.components.auth.AuthFooter
import com.example.ui_kit.components.auth.SignUpCard
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel
import com.example.ui_kit.components.auth.SocialLoginSection
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (user: AppUser) -> Unit
) {
    val viewModel: AuthViewModel = koinViewModel()
    val state = viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activity = remember(context) {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) break
            currentContext = currentContext.baseContext
        }
        currentContext as? Activity
    }

    LaunchedEffect(state.value) {
        if (state.value is AuthState.Success) {
            onNavigateToHome((state.value as AuthState.Success).user)
        }
    }

    SignUpContent(
        modifier = modifier,
        state = state.value,
        onSignUp = { name, email, password ->
            viewModel.signUp(name, email, password)
        },
        onLoginWithGoogle = {
            Log.d("SignUpScreen", "onLoginWithGoogle clicked")
            coroutineScope.launch {
                try {
                    if (activity == null) {
                        Log.e("SignUpScreen", "Activity context is null")
                        viewModel.setAuthError("Failed to initiate login: host activity is not available.")
                        return@launch
                    }
                    Log.d("SignUpScreen", "Initializing CredentialManager")
                    val credentialManager = CredentialManager.create(activity)
                    val webClientId = "50329480866-0ismrbov61kq0tj3c4g1282foev660r6.apps.googleusercontent.com"

                    Log.d("SignUpScreen", "Building GetGoogleIdOption")
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(false) // Set to false to always show the bottom sheet chooser
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    Log.d("SignUpScreen", "Requesting credential from CredentialManager")
                    val result = credentialManager.getCredential(activity, request)
                    val credential = result.credential

                    Log.d("SignUpScreen", "Credential retrieved. Type: ${credential.type}")
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Log.d("SignUpScreen", "Obtained Google ID Token successfully")
                        viewModel.signInWithGoogle(googleIdTokenCredential.idToken)
                    } else {
                        Log.w("SignUpScreen", "Unexpected credential type received")
                        viewModel.setAuthError("Sign-in failed: unexpected credential type.")
                    }
                } catch (e: GetCredentialCancellationException) {
                    Log.d("SignUpScreen", "User cancelled Google Sign-In")
                    viewModel.setIdleState()
                } catch (e: NoCredentialException) {
                    Log.w("SignUpScreen", "No credentials/accounts found: ${e.message}")
                    viewModel.setAuthError("No Google accounts found. Please add a Google account in your device settings.")
                } catch (e: Throwable) {
                    Log.e("SignUpScreen", "Error during Google Sign-In", e)
                    viewModel.setAuthError(e.message ?: "Google sign-in failed. Please try again.")
                }
            }
        },
        onLoginAsGuest = {
            onNavigateToHome(
                AppUser(
                    id = "Guest",
                    email = "alooo@alooo.com",
                )
            )
        },
        onNavigateToLogin = {
            onNavigateToLogin()
        }
    )
}

@Composable
private fun SignUpContent(
    modifier: Modifier = Modifier,
    state: AuthState,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    onLoginWithGoogle: () -> Unit,
    onLoginAsGuest: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val errorMessage = (state as? AuthState.Error)?.message

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Qafilah",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your journey to discovery begins here",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            SignUpCard(
                onSignUp = onSignUp,
                authErrorMessage = errorMessage
            )

            SocialLoginSection(
                googleIcon = painterResource(id = R.drawable.ic_google),
                onGoogleClick = onLoginWithGoogle,
                onAppleClick = {},
                onEmailClick = {}
            )

            AuthFooter(
                isInLogin = false,
                onLoginAsGuest = onLoginAsGuest,
                onNavigate = onNavigateToLogin
            )
        }

        if (state is AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},

                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
            }
        }
    }
}