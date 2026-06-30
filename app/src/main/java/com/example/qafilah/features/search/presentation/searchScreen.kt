package com.example.qafilah.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.search.domain.model.ChipState
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel
import com.example.ui_kit.components.search.SearchEmptyState
import com.example.ui_kit.components.search.TrendingPillCard
import com.example.ui_kit.components.shared.RemovableChip
import com.example.ui_kit.components.shared.SearchField

@Composable
fun SearchScreen(
    searchQuery: String,                               // 1. Expose query string state
    onSearchQueryChange: (String) -> Unit,             // 2. Expose callback to trigger search logic in ViewModel
    recentSearches: List<ChipState>,
    trendingSearches: List<String>,
    searchResults: List<ProductUiModel>,               // 3. Clean entry point for dynamic product lists
    onProductClick: (ProductUiModel) -> Unit,          // 4. Added navigation event callback
    onFavoriteClick: (ProductUiModel) -> Unit,         // 5. Added interactive state event callback
    onRemoveRecentSearch: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                SearchField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,   // Passes events directly back up
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (searchQuery.isEmpty()) {
                // --- INITIAL LANDING PAGE ---

                // Recent Searches Section
                if (recentSearches.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Searches",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(onClick = onClearAllRecentSearches) {
                                    Text(
                                        text = "CLEAR ALL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                recentSearches.forEach { chip ->
                                    RemovableChip(
                                        label = chip.name,
                                        onRemoveClick = { onRemoveRecentSearch(chip.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Trending Now Section
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Trending Now",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(trendingSearches) { searchItem ->
                    TrendingPillCard(
                        label = searchItem,
                        onClick = { onSearchQueryChange(searchItem) }
                    )
                }

                // Default Discover Section (Shows default recommended items if results are passed initially)
                if (searchResults.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Discover Products",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    items(searchResults) { product ->
                        ProductCard(
                            product = product,
                            onClick = onProductClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }
                }

            } else {
                // --- SYSTEM FILTERED RESULTS STATE ---
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (searchResults.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SearchEmptyState()
                    }
                } else {
                    items(searchResults) { product ->
                        ProductCard(
                            product = product,
                            onClick = onProductClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }
                }
            }
        }
    }
}