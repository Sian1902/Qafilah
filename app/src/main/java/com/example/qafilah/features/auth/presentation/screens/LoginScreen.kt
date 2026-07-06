package com.example.qafilah.features.auth.presentation.screens

import android.app.Activity
import android.content.ContextWrapper
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
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
import androidx.compose.ui.res.stringResource
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

    val errorHostActivityUnavailable = stringResource(R.string.error_host_activity_unavailable)
    val errorUnexpectedCredentialType = stringResource(R.string.error_unexpected_credential_type)
    val errorNoGoogleAccounts = stringResource(R.string.error_no_google_accounts)
    val errorGoogleSignInFailed = stringResource(R.string.error_google_sign_in_failed)

    val errorLoginFailed = stringResource(R.string.error_login_failed)
    val errorGoogleAuthFailed = stringResource(R.string.error_google_auth_failed)
    val errorCredentialsEmpty = stringResource(R.string.error_credentials_empty)
    val errorNoEmail = stringResource(R.string.no_email)
    val defaultFirstName = stringResource(R.string.valued_customer_first_name)
    val defaultLastName = stringResource(R.string.valued_customer_last_name)

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

    LoginContent(
        modifier = modifier,
        state = state.value,
        onLogin = { email, password ->
            viewModel.signIn(
                email,
                password,
                errorLoginFailed,
                errorCredentialsEmpty,
                defaultFirstName,
                defaultLastName
            )
        },
        onLoginWithGoogle = {
            coroutineScope.launch {
                try {

                    val credentialManager = CredentialManager.create(context)

                    val webClientId = "50329480866-0ismrbov61kq0tj3c4g1282foev660r6.apps.googleusercontent.com"

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(context, request)
                    val credential = result.credential

                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        viewModel.signInWithGoogle(
                            idToken,
                            errorGoogleAuthFailed,
                            errorNoEmail,
                            defaultFirstName,
                            defaultLastName
                        )
                    } else {
                        viewModel.setAuthError(errorUnexpectedCredentialType)
                    }
                } catch (e: GetCredentialCancellationException) {
                    viewModel.setIdleState()
                } catch (e: NoCredentialException) {
                    viewModel.setAuthError(errorNoGoogleAccounts)
                } catch (e: Throwable) {
                    viewModel.setAuthError(e.message ?: errorGoogleSignInFailed)
                }
            }
        },
        onLoginAsGuest = {
            onContinueAsGuest()
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
            logoContentDescription = stringResource(R.string.app_logo_content_description),
            titleText = stringResource(R.string.login_welcome_back_title),
            subtitleText = stringResource(R.string.login_welcome_back_subtitle)
        )

        LoginCard(
            onLogin = onLogin,
            authErrorMessage = errorMessage,
            googleIcon = painterResource(id = R.drawable.ic_google),
            onLoginWithGoogle = onLoginWithGoogle,
            emailLabel = stringResource(R.string.email_address),
            emailPlaceholder = stringResource(R.string.login_email_placeholder),
            emailErrorMessage = stringResource(R.string.login_email_error_message),
            passwordLabel = stringResource(R.string.password),
            passwordPlaceholder = stringResource(R.string.login_password_placeholder),
            passwordErrorMessage = stringResource(R.string.login_password_error_message),
            forgotPasswordLabel = stringResource(R.string.login_forgot_password_label),
            loginButtonLabel = stringResource(R.string.login_button_label),
            orLabel = stringResource(R.string.ui_common_or),
            togglePasswordVisibilityContentDescription = stringResource(R.string.login_toggle_password_visibility_cd)
        )

        AuthFooter(
            isInLogin = false,
            onLoginAsGuest = onLoginAsGuest,
            onNavigate = onNavigateToSignUp,
            loginPrompt = stringResource(R.string.login_prompt),
            loginActionLabel = stringResource(R.string.login_action_label),
            signupPrompt = stringResource(R.string.signup_prompt),
            signupActionLabel = stringResource(R.string.signup_action_label),
            guestButtonLabel = stringResource(R.string.guest_button_label),
            guestButtonContentDescription = stringResource(R.string.guest_button_content_description)
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
