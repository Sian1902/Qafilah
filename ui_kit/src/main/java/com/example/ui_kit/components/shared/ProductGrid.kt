package com.example.ui_kit.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel

@Composable
fun ProductGrid(
    products: List<ProductUiModel>,
    onProductClick: (ProductUiModel) -> Unit,
    onFavoriteClick: (ProductUiModel) -> Unit,
    emptyMessage: String,
    retryButtonLabel: String,
    toggleWishlistContentDescription: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isLoadingMore: Boolean = false,
    error: String? = null,
    onRetry: (() -> Unit)? = null,
    onLoadMore: (() -> Unit)? = null,
    loadMoreThreshold: Int = 4,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    listState: LazyListState = rememberLazyListState(),
    headerContent: (LazyListScope.() -> Unit)? = null
) {
    if (onLoadMore != null) {
        val reachedEnd by remember(listState) {
            derivedStateOf {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val total = listState.layoutInfo.totalItemsCount
                total > 0 && lastVisible >= total - loadMoreThreshold
            }
        }
        LaunchedEffect(reachedEnd) {
            if (reachedEnd && !isLoadingMore && !isLoading) onLoadMore()
        }
    }

    when {
        isLoading && products.isEmpty() -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return
        }

        error != null && products.isEmpty() -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    if (onRetry != null) {
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRetry) { Text(retryButtonLabel) }
                    }
                }
            }
            return
        }

        products.isEmpty() -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
            return
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        headerContent?.invoke(this)

        items(
            items = products.chunked(2),
            key = { row -> row.first().id }
        ) { rowProducts ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        product = product,
                        onClick = onProductClick,
                        onFavoriteClick = onFavoriteClick,
                        toggleWishlistContentDescription = toggleWishlistContentDescription,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowProducts.size < 2) Spacer(Modifier.weight(1f))
            }
        }

        if (isLoadingMore) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}