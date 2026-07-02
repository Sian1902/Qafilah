package com.example.qafilah.features.wishlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel
import com.example.ui_kit.components.login.LoginPromptBottomSheet
import com.example.ui_kit.components.shared.QafilahConfirmDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    onProductClick: (ProductUiModel) -> Unit,
    onFavoriteClick: (ProductUiModel) -> Unit,
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        strokeWidth = 6.dp
                    )
                }
                state.items.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Your wishlist is waiting to be filled.",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Begin your caravan journey by exploring our curated collections.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    // --- WISHLIST DYNAMIC GRID LIST VIEW ---
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Wishlist Header Titles Block
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column {
                                Text(
                                    text = "Wishlist",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${state.items.size} items curated for your journey",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Product Items Rendering Loop[cite: 14]
                        items(items = state.items, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = onProductClick,
                                onFavoriteClick = { viewModel.onIntent(WishlistIntent.PromptRemove(product = product)) })                     }
                    }
                }
            }
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
    state.productToConfirmRemove?.let { product ->
        QafilahConfirmDialog(
            title = "Remove Item",
            message = "Are you sure you want to remove ${product.name} from your wishlist?",
            onConfirm = { viewModel.onIntent(WishlistIntent.ConfirmRemoval) },
            onDismiss = { viewModel.onIntent(WishlistIntent.DismissRemoval) }
        )
    }
}