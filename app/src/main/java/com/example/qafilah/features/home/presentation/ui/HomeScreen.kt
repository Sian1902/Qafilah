package com.example.qafilah.features.home.presentation.ui

import android.util.Log
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.home.presentation.viewmodel.HomeUiState
import com.example.qafilah.features.home.presentation.viewmodel.HomeEvent
import com.example.qafilah.features.home.presentation.viewmodel.HomeViewModel
import com.example.ui_kit.components.home.BrandRow
import com.example.ui_kit.components.home.CategoryRow
import com.example.ui_kit.components.home.CategoryUiModel
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel
import com.example.ui_kit.components.shared.PromoBannerCard
import com.example.ui_kit.components.shared.SearchField
import com.example.ui_kit.components.shared.SectionHeader
import com.example.ui_kit.components.shared.WelcomeHeader
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds


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

    LaunchedEffect(Unit) {
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
                            text = uiState.error ?: "Something went wrong",
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
                        onFavoriteClick = { product -> viewModel.toggleFavorite(product.id) },
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
            WelcomeHeader(
                userName = "Traveler",
                avatarUrl = null,
                hasNotification = true,
                onNotificationClick = onNotificationClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            SearchField(
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
                title = "Categories",
                trailingText = "VIEW ALL",
                onTrailingClick = onViewAllCategoriesClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            CategoryRow(
                categories = uiState.categories,
                onCategoryClick = onCategoryClick
            )
        }

        item {
            SectionHeader(
                title = "Shop by Brand",
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
                title = "New Arrivals",
                trailingText = "${uiState.products.size} ITEMS",
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
                        modifier = Modifier.weight(1f)
                    )
                    Log.d("id", "Product: ${product.id}")
                }
                if (rowProducts.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

