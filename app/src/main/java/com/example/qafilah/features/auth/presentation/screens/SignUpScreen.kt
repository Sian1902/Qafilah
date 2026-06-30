package com.example.qafilah.features.auth.presentation.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.ui_kit.components.auth.SocialLoginSection
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (user: AppUser) -> Unit
) {
    val viewModel: AuthViewModel = koinViewModel()
    val state = viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(state.value) {
        if (state.value is AuthState.Success) {
            onNavigateToHome(
                (state.value as AuthState.Success).user
            )
        }
    }

    SignUpContent(
        modifier = modifier,
        state = state.value,
        onSignUp = { name, email, password ->
            viewModel.signUp(email, password)
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
                onGoogleClick = { },
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
                    ) {
                    },
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
fun SignUpScreenPreview() {
    QafilahTheme(darkTheme = true) {
        Surface {
            SignUpContent(
                state = AuthState.Idle,
                onSignUp = { _, _, _ -> },
                onLoginAsGuest = {},
                onNavigateToLogin = {}
            )
        }
    }
}
