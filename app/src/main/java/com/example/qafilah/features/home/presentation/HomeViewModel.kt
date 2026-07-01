package com.example.qafilah.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.R
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import com.example.ui_kit.components.home.CategoryUiModel
import com.example.ui_kit.components.home.ProductUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PRODUCTS_LIMIT = 10
private const val COLLECTIONS_LIMIT = 10

sealed interface HomeEvent {
    data class ShowSnackbar(val message: String) : HomeEvent
}

class HomeViewModel(
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var domainProductsCache: List<Product> = emptyList()

    init {
        loadHome()
    }

    fun loadHome() {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val products = getBestSellingUseCase(limit = PRODUCTS_LIMIT, after = null)
                val collections = getCollectionsUseCase(limit = COLLECTIONS_LIMIT, after = null)
                domainProductsCache = products
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = staticCategories,
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


    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun toggleFavorite(productId: String) {
        val uiProduct = _uiState.value.products.find { it.id == productId } ?: return
        val domainProduct = domainProductsCache.find { it.id == productId } ?: return

        val wasFavorite = uiProduct.isFavorite

        _uiState.update { state ->
            state.copy(
                products = state.products.map { p ->
                    if (p.id == productId) p.copy(isFavorite = !wasFavorite) else p
                }
            )
        }

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
                        currencyCode = "USD"
                    )
                    _events.trySend(HomeEvent.ShowSnackbar("${domainProduct.title} added to wishlist"))
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        products = state.products.map { p ->
                            if (p.id == productId) p.copy(isFavorite = wasFavorite) else p
                        },
                        error = e.message ?: "Failed to update wishlist"
                    )
                }
            }
        }
    }
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

private fun StoreCollection.toBrandLabel(): String = title

private val staticCategories = listOf(
    CategoryUiModel("jewelry", "JEWELRY", R.drawable.onboarding_ring),
    CategoryUiModel("attire", "ATTIRE", R.drawable.onboarding_fabric),
    CategoryUiModel("scent", "SCENT", R.drawable.ic_logo),
    CategoryUiModel("home", "HOME", R.drawable.home),
    CategoryUiModel("gear", "GEAR", R.drawable.onboarding_bag)
)