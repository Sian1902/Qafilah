package com.example.qafilah.features.search.presentation

import com.example.qafilah.features.search.domain.model.ChipState
import com.example.ui_kit.components.home.ProductUiModel

data class SearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val recentSearches: List<ChipState> = emptyList(),
    val trendingSearches: List<String> = emptyList(),
    val searchResults: List<ProductUiModel> = emptyList(),
    val availableCategories: List<ChipState> = emptyList(),
    val availableBrands: List<ChipState> = emptyList(),
    val selectedCategory: String? = null,
    val selectedBrand: String? = null
)