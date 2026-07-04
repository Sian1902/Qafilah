package com.example.qafilah.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.R
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.core.currency.ConvertPriceUseCase
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
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
    private val isProductWishlistedUseCase: IsProductWishlistedUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var domainProductsCache: List<Product> = emptyList()

    fun loadHome(fallbackErrorMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val products = getBestSellingUseCase(limit = PRODUCTS_LIMIT, after = null)
                val collections = getCollectionsUseCase(limit = COLLECTIONS_LIMIT, after = null)
                domainProductsCache = products

                val uiProducts = products.map { product ->
                    val convertedPrice = convertPriceUseCase(product.priceAmount.toDoubleOrNull() ?: 0.0)
                    product.toUiModel(convertedPrice)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = staticCategories,
                        brands = collections.map { it.toBrandLabel() },
                        products = uiProducts
                    )
                }

                products.forEach { product -> observeWishlistState(product.id) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: fallbackErrorMessage)
                }
            }
        }
    }

    private fun observeWishlistState(productId: String) {
        viewModelScope.launch {
            isProductWishlistedUseCase(productId).collect { isWishlisted ->
                _uiState.update { state ->
                    state.copy(
                        products = state.products.map { product ->
                            if (product.id == productId) product.copy(isFavorite = isWishlisted)
                            else product
                        }
                    )
                }
            }
        }
    }

    fun toggleFavorite(
        productId: String,
        addedToWishlistTemplate: String,
        fallbackErrorMessage: String
    ) {
        val uiProduct = _uiState.value.products.find { it.id == productId } ?: return
        val domainProduct = domainProductsCache.find { it.id == productId } ?: return
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
                        currencyCode = "USD"
                    )
                    _events.trySend(
                        HomeEvent.ShowSnackbar(
                            String.format(
                                addedToWishlistTemplate,
                                domainProduct.title
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: fallbackErrorMessage)
                }
            }
        }
    }
}

private fun Product.toUiModel(displayPrice: String): ProductUiModel = ProductUiModel(
    id = id,
    imageUrl = imageUrl.orEmpty(),
    category = vendor,
    name = title,
    price = displayPrice,
    badge = null,
    isFavorite = false
)

private fun StoreCollection.toBrandLabel(): String = title

private val staticCategories = listOf(
    CategoryUiModel("jewelry", R.string.category_jewelry, R.drawable.onboarding_ring),
    CategoryUiModel("attire", R.string.category_attire, R.drawable.onboarding_fabric),
    CategoryUiModel("scent", R.string.category_scent, R.drawable.ic_logo),
    CategoryUiModel("home", R.string.category_home, R.drawable.home),
    CategoryUiModel("gear", R.string.category_gear, R.drawable.onboarding_bag)
)
