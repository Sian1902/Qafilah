package com.example.qafilah.features.auth.presentation.screens

import android.util.Log
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CustomCredential
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.R
import com.example.qafilah.features.auth.domain.model.AppUser 
import com.example.qafilah.features.auth.presentation.AuthState 
import com.example.qafilah.features.auth.presentation.AuthViewModel 
import com.example.ui_kit.components.auth.AuthFooter 
import com.example.ui_kit.components.auth.LoginCard 
import com.example.ui_kit.components.auth.LoginTitle 
import com.example.ui_kit.theme.QafilahTheme 
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: (user: AppUser) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val viewModel: AuthViewModel = koinViewModel()
    val state = viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activity = remember(context) {
        var currentContext = context
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is android.app.Activity) break
            currentContext = currentContext.baseContext
        }
        currentContext as? android.app.Activity
    }

    LaunchedEffect(state.value) {
        if (state.value is AuthState.Success) {
            onNavigateToHome((state.value as AuthState.Success).user)
        }
    }

    LoginContent(
        modifier = modifier,
        state = state.value,
        onLogin = { email, password ->
            viewModel.signIn(email, password)
        },
        onLoginWithGoogle = {
            Log.d("LoginScreen", "onLoginWithGoogle clicked")
            coroutineScope.launch {
                try {
                    if (activity == null) {
                        Log.e("LoginScreen", "Activity context is null")
                        viewModel.setAuthError("Failed to initiate login: host activity is not available.")
                        return@launch
                    }
                    Log.d("LoginScreen", "Initializing CredentialManager")
                    val credentialManager = CredentialManager.create(activity)

                    val webClientId = "50329480866-0ismrbov61kq0tj3c4g1282foev660r6.apps.googleusercontent.com"

                    Log.d("LoginScreen", "Building GetGoogleIdOption")
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    Log.d("LoginScreen", "Requesting credential from CredentialManager")
                    val result = credentialManager.getCredential(activity, request)
                    val credential = result.credential

                    Log.d("LoginScreen", "Credential retrieved. Type: ${credential.type}")
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        Log.d("LoginScreen", "Obtained Google ID Token successfully")

                        viewModel.signInWithGoogle(idToken)
                    } else {
                        Log.w("LoginScreen", "Unexpected credential type received")
                        viewModel.setAuthError("Sign-in failed: unexpected credential type.")
                    }
                } catch (e: GetCredentialCancellationException) {
                    Log.d("LoginScreen", "User cancelled Google Sign-In")
                    viewModel.setIdleState()
                } catch (e: NoCredentialException) {
                    Log.w("LoginScreen", "No credentials/accounts found: ${e.message}")
                    viewModel.setAuthError("No Google accounts found. Please add a Google account in your device settings.")
                } catch (e: Throwable) {
                    Log.e("LoginScreen", "Error during Google Sign-In", e)
                    viewModel.setAuthError(e.message ?: "Google sign-in failed. Please try again.")
                }
            }
        },
        onLoginAsGuest = {
            onNavigateToHome(AppUser(id = "Guest", email = "alooo@alooo.com"))
        },
        onNavigateToSignUp = {
            onNavigateToSignUp()
        }
    )
}

@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    state: AuthState,
    onLogin: (email: String, password: String) -> Unit,
    onLoginWithGoogle: () -> Unit,
    onLoginAsGuest: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val errorMessage = (state as? AuthState.Error)?.message 
    val focusManager = LocalFocusManager.current 
    Box(
        modifier = modifier
            .fillMaxSize() 
    .background(MaterialTheme.colorScheme.background) 
    .pointerInput(Unit) { 
        detectTapGestures(onTap = { 
            focusManager.clearFocus() 
        })
    }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() 
        .verticalScroll(rememberScrollState()), 
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.SpaceBetween 
        ) {
        Spacer(modifier = Modifier.height(48.dp)) 

        LoginTitle(
            logoPainter = painterResource(id = R.drawable.ic_logo), 
        )

        LoginCard(
            onLogin = onLogin, 
        authErrorMessage = errorMessage, 
        googleIcon = painterResource(id = R.drawable.ic_google), 
        onLoginWithGoogle = onLoginWithGoogle )

        AuthFooter(
            isInLogin = true, 
        onLoginAsGuest = onLoginAsGuest, 
        onNavigate = onNavigateToSignUp 
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

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    QafilahTheme(darkTheme = true) { 
        Surface { 
            LoginContent(
                state = AuthState.Idle, 
            onLogin = { _, _ -> }, 
            onLoginWithGoogle = {}, 
            onLoginAsGuest = {}, 
            onNavigateToSignUp = {} 
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreviewLight() {
    QafilahTheme(darkTheme = false) { 
        Surface { 
            LoginContent(
                state = AuthState.Idle, 
            onLogin = { _, _ -> }, 
            onLoginWithGoogle = {}, 
            onLoginAsGuest = {},
            onNavigateToSignUp = {}
            )
        }
    }
}