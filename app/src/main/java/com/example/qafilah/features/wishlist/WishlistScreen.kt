package com.example.qafilah.features.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.features.wishlist.presentation.WishlistEvent
import com.example.qafilah.features.wishlist.presentation.WishlistIntent
import com.example.qafilah.features.wishlist.presentation.WishlistViewModel
import com.example.ui_kit.components.login.LoginPromptBottomSheet
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(WishlistIntent.EnterScreen)
        viewModel.events.collect { event ->
            when (event) {
                WishlistEvent.NavigateToLogin -> onNavigateToLogin()
                WishlistEvent.NavigateToSignUp -> onNavigateToSignUp()
                WishlistEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp
            )
        }
    }

    if (state.showLoginPrompt) {
        LoginPromptBottomSheet(
            featureName = "your wishlist",
            onDismiss = { viewModel.onIntent(WishlistIntent.DismissLoginPrompt) },
            onNavigateToLogin = { viewModel.onIntent(WishlistIntent.NavigateToLogin) },
            onNavigateToSignUp = { viewModel.onIntent(WishlistIntent.NavigateToSignUp) }
        )
    }
}
