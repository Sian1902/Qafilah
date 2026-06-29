package com.example.qafilah.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.qafilah.features.auth.presentation.AuthState
import com.example.qafilah.features.auth.presentation.AuthViewModel
import com.example.ui_kit.components.auth.AuthFooter
import com.example.ui_kit.components.auth.LoginCard
import com.example.ui_kit.components.auth.LoginTitle
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel
import com.example.qafilah.R
import com.example.qafilah.features.auth.domain.model.AppUser

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToSignUp: () -> Unit,
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

    LoginContent(
        modifier = modifier,
        state = state.value,
        onLogin = { email, password ->
            viewModel.signIn(email, password)
        },
        onLoginAsGuest = {
            onNavigateToHome(AppUser(
                id = "Guest",
                email = "alooo@alooo.com",
            ))
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
    onLoginAsGuest: () -> Unit,
    onNavigateToSignUp: () -> Unit
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

            LoginTitle(
                logoPainter = painterResource(id = R.drawable.ic_logo),
            )

            LoginCard(
                onLogin = onLogin,
                authErrorMessage = errorMessage
            )

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
                    ) {
                        // nothing underneath gets triggered
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
fun LoginScreenPreview() {
    QafilahTheme(darkTheme = true) {
        Surface {
            LoginContent(
                state = AuthState.Idle,
                onLogin = { _, _ -> },
                onLoginAsGuest = {},
                onNavigateToSignUp = {}
            )
        }
    }
}