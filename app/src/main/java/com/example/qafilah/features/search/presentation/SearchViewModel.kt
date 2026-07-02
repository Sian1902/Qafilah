package com.example.qafilah.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.SearchProductsUseCase
import com.example.qafilah.features.search.data.datasource.SearchLocalDataSource
import com.example.qafilah.features.search.domain.model.ChipState
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import com.example.ui_kit.components.home.ProductUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val localDataSource: SearchLocalDataSource,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val isProductWishlistedUseCase: IsProductWishlistedUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var rawFetchedProducts: List<Product> = emptyList()
    private var searchJob: Job? = null

    init {
        loadRecentSearches()
        loadTrendingAndFilters()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (newQuery.isNotBlank()) {
                executeSearch(newQuery)
            } else {
                _uiState.update { it.copy(searchResults = emptyList(), isLoading = false) }
            }
        }
    }

    fun commitSearchQuery(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            localDataSource.saveSearchQuery(query)
            loadRecentSearches()
        }
    }

    private fun loadRecentSearches() {
        val cached = localDataSource.getRecentSearches().mapIndexed { index, query ->
            ChipState(id = index.toString(), name = query)
        }
        _uiState.update { it.copy(recentSearches = cached) }
    }

    private fun loadTrendingAndFilters() {
        viewModelScope.launch {
            try {
                val trendingProducts = getBestSellingUseCase(limit = 6, after = null)
                val trendingNames = trendingProducts.map { it.title }

                val collections = getCollectionsUseCase(limit = 20, after = null)
                val brandChips = collections.map {
                    ChipState(id = it.title, name = it.title)
                }

                val categories = listOf("jewelry", "attire", "scent", "home", "gear").map {
                    ChipState(id = it, name = it.uppercase())
                }

                _uiState.update {
                    it.copy(
                        trendingSearches = trendingNames,
                        availableCategories = categories,
                        availableBrands = brandChips
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private suspend fun executeSearch(query: String) {
        _uiState.update { it.copy(isLoading = true) }
        searchProductsUseCase(query = query, limit = 50).onSuccess { products ->
            rawFetchedProducts = products
            combineAndEmitResults()
        }.onFailure { error ->
            _uiState.update { it.copy(isLoading = false, error = error.message) }
        }
    }

    fun toggleCategoryFilter(categoryId: String) {
        _uiState.update { state ->
            val newSelection = if (state.selectedCategory == categoryId) null else categoryId
            state.copy(
                selectedCategory = newSelection,
                availableCategories = state.availableCategories.map { it.copy(isSelected = it.id == newSelection) }
            )
        }
        combineAndEmitResults()
    }

    fun toggleBrandFilter(brandId: String) {
        _uiState.update { state ->
            val newSelection = if (state.selectedBrand == brandId) null else brandId
            state.copy(
                selectedBrand = newSelection,
                availableBrands = state.availableBrands.map { it.copy(isSelected = it.id == newSelection) }
            )
        }
        combineAndEmitResults()
    }

    fun resetFilters() {
        _uiState.update { state ->
            state.copy(
                selectedCategory = null,
                selectedBrand = null,
                availableCategories = state.availableCategories.map { it.copy(isSelected = false) },
                availableBrands = state.availableBrands.map { it.copy(isSelected = false) }
            )
        }
        combineAndEmitResults()
    }

    private fun combineAndEmitResults() {
        val categoryFilter = _uiState.value.selectedCategory
        val brandFilter = _uiState.value.selectedBrand

        val filtered = rawFetchedProducts.filter { product ->
            val matchesCategory = categoryFilter == null || product.vendor.equals(categoryFilter, ignoreCase = true)
            val matchesBrand = brandFilter == null || product.vendor.equals(brandFilter, ignoreCase = true)
            matchesCategory && matchesBrand
        }.map { it.toUiModel() }

        _uiState.update { it.copy(searchResults = filtered, isLoading = false) }

        filtered.forEach { observeWishlistState(it.id) }
    }

    private fun observeWishlistState(productId: String) {
        viewModelScope.launch {
            isProductWishlistedUseCase(productId).collect { isWishlisted ->
                _uiState.update { state ->
                    state.copy(
                        searchResults = state.searchResults.map { uiProduct ->
                            if (uiProduct.id == productId) uiProduct.copy(isFavorite = isWishlisted) else uiProduct
                        }
                    )
                }
            }
        }
    }

    fun toggleFavorite(productId: String) {
        val uiProduct = _uiState.value.searchResults.find { it.id == productId } ?: return
        val domainProduct = rawFetchedProducts.find { it.id == productId } ?: return
        val wasFavorite = uiProduct.isFavorite

        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    removeFromWishlistUseCase(productId)
                } else {
                    addToWishlistUseCase(
                        productId = domainProduct.id,
                        handle = domainProduct.id,
                        title = domainProduct.title,
                        imageUrl = domainProduct.imageUrl,
                        vendor = domainProduct.vendor,
                        price = domainProduct.priceAmount.toDoubleOrNull() ?: 0.0,
                        currencyCode = domainProduct.currencyCode
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun removeRecentSearch(query: String) {
        localDataSource.removeSearchQuery(query)
        loadRecentSearches()
    }

    fun clearAllRecentSearches() {
        localDataSource.clearAll()
        loadRecentSearches()
    }

    private fun Product.toUiModel(): ProductUiModel = ProductUiModel(
        id = id,
        imageUrl = imageUrl.orEmpty(),
        category = vendor,
        name = title,
        price = "$currencyCode $priceAmount",
        badge = null,
        isFavorite = false
    )
}