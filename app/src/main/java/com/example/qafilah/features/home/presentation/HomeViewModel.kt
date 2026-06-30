package com.example.qafilah.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.R
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.ui_kit.components.home.CategoryUiModel
import com.example.ui_kit.components.home.ProductUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PRODUCTS_LIMIT = 10
private const val COLLECTIONS_LIMIT = 10


class HomeViewModel(
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val products = getBestSellingUseCase(limit = PRODUCTS_LIMIT, after = null)
                val collections = getCollectionsUseCase(limit = COLLECTIONS_LIMIT, after = null)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = staticCategories, // TODO: swap for a GetCategoriesUseCase once it exists
                        brands = collections.map { collection -> collection.toBrandLabel() },
                        products = products.map { product ->
                            product.toUiModel()
                        }

                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Something went wrong")
                }
            }
        }

    }


    fun toggleFavorite(productId: String) {
        _uiState.update { state ->
            state.copy(
                products = state.products.map { product ->
                    if (product.id == productId) product.copy(isFavorite = !product.isFavorite) else product
                }
            )
        }
    }
}

private fun Product.toUiModel(): ProductUiModel = ProductUiModel(
    id = id,
    imageUrl = imageUrl.orEmpty(),
    category = vendor,
    name = title,
    price = priceAmount,
    isFavorite = false
)

private fun StoreCollection.toBrandLabel(): String = title

private val staticCategories = listOf(
    CategoryUiModel("jewelry", "JEWELRY", R.drawable.onboarding_ring),
    CategoryUiModel("attire", "ATTIRE", R.drawable.onboarding_fabric),
    CategoryUiModel("scent", "SCENT", R.drawable.ic_logo),
    CategoryUiModel("home", "HOME", R.drawable.home),
    CategoryUiModel("gear", "GEAR", R.drawable.onboarding_bag)
)