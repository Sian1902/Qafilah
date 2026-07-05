package com.example.qafilah.features.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.home.presentation.viewmodel.HomeUiState
import com.example.qafilah.features.home.presentation.viewmodel.HomeEvent
import com.example.qafilah.features.home.presentation.viewmodel.HomeViewModel
import com.example.ui_kit.components.home.BrandRow
import com.example.ui_kit.components.home.CategoryRow
import com.example.ui_kit.components.home.CategoryUiModel
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel
import com.example.ui_kit.components.shared.SearchField
import com.example.ui_kit.components.shared.SectionHeader
import com.example.ui_kit.components.shared.WelcomeHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onBrandClick: (String) -> Unit,
    onProductClick: (ProductUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val homeErrorFallback = stringResource(R.string.error_something_went_wrong)
    val wishlistAddedTemplate = stringResource(R.string.wishlist_item_added)
    val wishlistErrorFallback = stringResource(R.string.error_failed_to_update_wishlist)
    val notLoggedInMessage = stringResource(R.string.error_login_to_manage_wishlist)

    LaunchedEffect(Unit) {
        viewModel.loadHome(homeErrorFallback)
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading && uiState.products.isEmpty() -> {
                    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null && uiState.products.isEmpty() -> {
                    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.error ?: homeErrorFallback,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    HomeContent(
                        uiState = uiState,
                        onSearchClick = onSearchClick,
                        onNotificationClick = onNotificationClick,
                        onCategoryClick = onCategoryClick,
                        onViewAllCategoriesClick = onViewAllCategoriesClick,
                        onBrandClick = onBrandClick,
                        onProductClick = onProductClick,
                        onFavoriteClick = { product ->
                            viewModel.toggleFavorite(
                                product.id,
                                wishlistAddedTemplate,
                                wishlistErrorFallback,
                                notLoggedInMessage = notLoggedInMessage
                            )
                        },
                        onClaimPromo = { code -> viewModel.claimPromoCode(code) },
                        modifier = modifier
                    )
                }
            }
        }
    }
}


@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onBrandClick: (String) -> Unit,
    onProductClick: (ProductUiModel) -> Unit,
    onFavoriteClick: (ProductUiModel) -> Unit,
    onClaimPromo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    )
    {
        item {
            val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val userName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: stringResource(R.string.default_username)
            val avatarUrl = currentUser?.photoUrl?.toString()

            WelcomeHeader(
                avatarUrl = avatarUrl,
                userAvatarContentDescription = stringResource(R.string.welcome_avatar_cd),
                welcomeBackLabel = stringResource(R.string.welcome_back_label),
                welcomeUserLabel = stringResource(R.string.welcome_user_greeting, userName),
                notificationsContentDescription = stringResource(R.string.welcome_notifications_cd),
                hasNotification = true,
                onNotificationClick = onNotificationClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            SearchField(
                placeholderText = stringResource(R.string.search_placeholder),
                searchIconContentDescription = stringResource(R.string.search_icon_cd),
                onClick = onSearchClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        if (uiState.promos.isNotEmpty()) {
            item {
                PromosPager(
                    promos = uiState.promos,
                    onClaimPromo = onClaimPromo
                )
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.brands),
                trailingText = stringResource(R.string.view_all),
                onTrailingClick = onViewAllCategoriesClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            CategoryRow(
                categories = uiState.categories,
                onCategoryClick = onCategoryClick,
                imageContentDescription = stringResource(R.string.category_image_cd)
            )
        }

        item {
            SectionHeader(
                title = stringResource(R.string.categories),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            BrandRow(
                brands = uiState.brands,
                onBrandClick = onBrandClick
            )
        }

        item {
            SectionHeader(
                title = stringResource(R.string.new_arrivals),
                trailingText = stringResource(R.string.new_arrivals_count, uiState.products.size),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        items(uiState.products.chunked(2)) { rowProducts ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        product = product,
                        onClick = onProductClick,
                        onFavoriteClick = onFavoriteClick,
                        toggleWishlistContentDescription = stringResource(R.string.product_card_toggle_wishlist_cd),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowProducts.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
