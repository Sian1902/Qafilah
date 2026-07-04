package com.example.qafilah.features.catalog.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.example.qafilah.R
import org.koin.androidx.compose.koinViewModel
import com.example.ui_kit.components.shared.ProductGrid
import com.example.ui_kit.components.home.ProductUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogProductsScreen(
    categoryId: String,
    categoryTitle: String,
    onBackClick: () -> Unit,
    onProductClick: (ProductUiModel) -> Unit
) {
    val vm: CatalogViewModel = koinViewModel()
    val products by vm.list.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(categoryId) {
        vm.loadCategoryProducts(categoryId)
    }

    val uiProducts = remember(products) {
        products.map { product ->
            ProductUiModel(
                id = product.id,
                imageUrl = product.imageUrl.orEmpty(),
                category = product.vendor,
                name = product.title,
                price = "${product.currencyCode} ${product.priceAmount}",
                isFavorite = false
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        ProductGrid(
            products = uiProducts,
            contentPadding = paddingValues,
            isLoading = isLoading,
            onProductClick = onProductClick,
            onFavoriteClick = { uiProduct ->
                val domainProduct = products.find { it.id == uiProduct.id }
                if (domainProduct != null) {
                    vm.toggleFavorite(domainProduct, uiProduct.isFavorite)
                }
            },
            emptyMessage = stringResource(R.string.product_grid_empty_message),
            retryButtonLabel = stringResource(R.string.retry),
            toggleWishlistContentDescription = stringResource(R.string.product_card_toggle_wishlist_cd),
            error = if (uiProducts.isEmpty() && !isLoading) stringResource(R.string.error_something_went_wrong) else null
        )
    }
}