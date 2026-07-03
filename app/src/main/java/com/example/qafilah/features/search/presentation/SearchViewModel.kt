package com.example.qafilah.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionProductsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetProductTypesUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetProductsByTypeUseCase
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

private const val RESULT_LIMIT = 50

class SearchViewModel(
    private val localDataSource: SearchLocalDataSource,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val getCollectionProductsUseCase: GetCollectionProductsUseCase,
    private val getProductTypesUseCase: GetProductTypesUseCase,
    private val getProductsByTypeUseCase: GetProductsByTypeUseCase,
    private val isProductWishlistedUseCase: IsProductWishlistedUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var rawFetchedProducts: List<Product> = emptyList()
    private var searchJob: Job? = null
    private var filtersReady = false
    private var pendingInitialCategory: String? = null
    private var pendingInitialBrand: String? = null
    private var brandNameToId: Map<String, String> = emptyMap()

    init {
        loadRecentSearches()
        loadTrendingAndFilters()
    }

    fun applyInitialFilters(category: String?, brand: String?) {
        if (category == null && brand == null) return

        if (!filtersReady) {
            pendingInitialCategory = category
            pendingInitialBrand = brand
            return
        }

        _uiState.update { state ->
            state.copy(
                selectedCategory = category ?: state.selectedCategory,
                selectedBrand = brand ?: state.selectedBrand,
                availableCategories = state.availableCategories.map {
                    it.copy(isSelected = it.id == (category ?: state.selectedCategory))
                },
                availableBrands = state.availableBrands.map {
                    it.copy(isSelected = it.id == (brand ?: state.selectedBrand))
                }
            )
        }

        fetchResults()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            fetchResults()
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
                brandNameToId = collections.associate { it.title to it.id }
                val brandChips = collections.map { ChipState(id = it.title, name = it.title) }

                val productTypes = getProductTypesUseCase(limit = 20)
                val categoryChips = productTypes.map { ChipState(id = it, name = it) }

                _uiState.update {
                    it.copy(
                        trendingSearches = trendingNames,
                        availableCategories = categoryChips,
                        availableBrands = brandChips
                    )
                }

                filtersReady = true
                if (pendingInitialCategory != null || pendingInitialBrand != null) {
                    applyInitialFilters(pendingInitialCategory, pendingInitialBrand)
                    pendingInitialCategory = null
                    pendingInitialBrand = null
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun fetchResults() {
        searchJob?.cancel()

        val state = _uiState.value
        val query = state.searchQuery.trim()
        val category = state.selectedCategory
        val brand = state.selectedBrand

        if (query.isBlank() && category == null && brand == null) {
            _uiState.update { it.copy(searchResults = emptyList(), isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val products = when {
                    query.isNotBlank() -> fetchByText(query, category, brand)
                    brand != null -> fetchByBrand(brand, category)
                    category != null -> getProductsByTypeUseCase(category, RESULT_LIMIT)
                    else -> emptyList()
                }
                rawFetchedProducts = products
                val uiModels = products.map { it.toUiModel() }
                _uiState.update { it.copy(searchResults = uiModels, isLoading = false) }
                uiModels.forEach { observeWishlistState(it.id) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun fetchByText(query: String, category: String?, brand: String?): List<Product> {
        val result = searchProductsUseCase(query = query, limit = RESULT_LIMIT)
        val products = result.getOrElse { throw it }
        return products.filter { product ->
            val matchesCategory = category == null || product.productType.equals(category, ignoreCase = true)
            val matchesBrand = brand == null || product.vendor.equals(brand, ignoreCase = true)
            matchesCategory && matchesBrand
        }
    }

    private suspend fun fetchByBrand(brand: String, category: String?): List<Product> {
        val collectionId = brandNameToId[brand] ?: return emptyList()
        val collectionWithProducts = getCollectionProductsUseCase(collectionId)
        val products = collectionWithProducts.products
        return if (category != null) {
            products.filter { it.productType.equals(category, ignoreCase = true) }
        } else {
            products
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
        fetchResults()
    }

    fun toggleBrandFilter(brandId: String) {
        _uiState.update { state ->
            val newSelection = if (state.selectedBrand == brandId) null else brandId
            state.copy(
                selectedBrand = newSelection,
                availableBrands = state.availableBrands.map { it.copy(isSelected = it.id == newSelection) }
            )
        }
        fetchResults()
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
        fetchResults()
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
            } catch (_: Exception) { }
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