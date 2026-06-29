package com.example.qafilah.features.home.presentation

import com.example.ui_kit.components.home.CategoryUiModel
import com.example.ui_kit.components.home.ProductUiModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val categories: List<CategoryUiModel> = emptyList(),
    val brands: List<String> = emptyList(),
    val products: List<ProductUiModel> = emptyList()
)