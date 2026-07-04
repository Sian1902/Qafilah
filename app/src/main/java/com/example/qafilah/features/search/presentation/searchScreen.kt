package com.example.qafilah.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.search.domain.model.ChipState
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel
import com.example.ui_kit.components.search.SearchEmptyState
import com.example.ui_kit.components.search.TrendingPillCard
import com.example.ui_kit.components.shared.RemovableChip
import com.example.ui_kit.components.shared.SearchField
import kotlinx.coroutines.launch

@Composable
private fun categoryDisplayName(categoryId: String): String {
    return when (categoryId) {
        "jewelry" -> stringResource(R.string.category_jewelry)
        "attire" -> stringResource(R.string.category_attire)
        "scent" -> stringResource(R.string.category_scent)
        "home" -> stringResource(R.string.category_home)
        "gear" -> stringResource(R.string.category_gear)
        else -> categoryId
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    recentSearches: List<ChipState>,
    trendingSearches: List<String>,
    searchResults: List<ProductUiModel>,
    availableCategories: List<ChipState>,
    availableBrands: List<ChipState>,
    onCategoryFilterSelect: (String) -> Unit,
    onBrandFilterSelect: (String) -> Unit,
    onResetFilters: () -> Unit,
    onProductClick: (ProductUiModel) -> Unit,
    onFavoriteClick: (ProductUiModel) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    onBackClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val filtersText = stringResource(R.string.filters)
    val resetAllText = stringResource(R.string.reset_all)
    val categoriesText = stringResource(R.string.categories)
    val brandsText = stringResource(R.string.brands)
    val applyFiltersText = stringResource(R.string.apply_filters)
    val goBackDescription = stringResource(R.string.go_back)
    val recentSearchesText = stringResource(R.string.recent_searches)
    val clearAllText = stringResource(R.string.clear_all)
    val trendingNowText = stringResource(R.string.trending_now)
    val resultsText = stringResource(R.string.search_results)
    val trendingIconDesc = stringResource(R.string.trending_icon_cd)

    val hasActiveFilter = availableCategories.any { it.isSelected } || availableBrands.any { it.isSelected }
    val showResults = searchQuery.isNotEmpty() || hasActiveFilter

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(filtersText, style = MaterialTheme.typography.headlineMedium)
                        TextButton(onClick = onResetFilters) {
                            Text(resetAllText)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(categoriesText, style = MaterialTheme.typography.titleMedium)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableCategories.forEach { category ->
                                FilterChip(
                                    selected = category.isSelected,
                                    onClick = { onCategoryFilterSelect(category.id) },
                                    label = { Text(categoryDisplayName(category.id)) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(brandsText, style = MaterialTheme.typography.titleMedium)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableBrands.forEach { brand ->
                                FilterChip(
                                    selected = brand.isSelected,
                                    onClick = { onBrandFilterSelect(brand.id) },
                                    label = { Text(brand.name) }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(applyFiltersText)
                    }
                }
            }
        }
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
                            contentDescription = goBackDescription,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    SearchField(
                        placeholderText = stringResource(R.string.search_placeholder),
                        searchIconContentDescription = stringResource(R.string.search_icon_cd),
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = filtersText,
                            tint = if (hasActiveFilter) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (!showResults) {
                        if (recentSearches.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = recentSearchesText,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        TextButton(onClick = onClearAllRecentSearches) {
                                            Text(
                                                text = clearAllText,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        recentSearches.forEach { chip ->
                                            RemovableChip(
                                                label = chip.name,
                                                removeContentDescription = stringResource(R.string.search_remove_chip_cd),
                                                onRemoveClick = { onRemoveRecentSearch(chip.name) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = trendingNowText,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(trendingSearches) { searchItem ->
                            TrendingPillCard(
                                label = searchItem,
                                iconContentDescription = trendingIconDesc,
                                onClick = { onSearchQueryChange(searchItem) }
                            )
                        }
                    } else {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = resultsText,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (searchResults.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SearchEmptyState(
                                    title = stringResource(R.string.search_empty_title),
                                    description = stringResource(R.string.search_empty_description),
                                    iconContentDescription = stringResource(R.string.search_icon_cd)
                                )
                            }
                        } else {
                            items(searchResults) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = onProductClick,
                                    onFavoriteClick = onFavoriteClick,
                                    toggleWishlistContentDescription = stringResource(R.string.product_card_toggle_wishlist_cd)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
