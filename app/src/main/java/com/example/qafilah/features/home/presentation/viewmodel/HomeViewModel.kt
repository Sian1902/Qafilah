package com.example.qafilah.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.R
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.SaveAdCouponUseCase
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
    private val saveAdCouponUseCase: SaveAdCouponUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            promos = promos
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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
                        // 1. REPLACED staticCategories with the actual fetched collections
                        categories = collections.map { collection ->
                            CategoryUiModel(
                                id = collection.id,
                                label = collection.title.uppercase(),
                                imageUrl = collection.imageUrl
                            )
                        },
                        brands = collections.map { it.toBrandLabel() },
                        products = products.map { it.toUiModel() }
                    )
                }

                products.forEach { product -> observeWishlistState(product.id) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Something went wrong")
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

    fun toggleFavorite(productId: String) {
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
                    _events.trySend(HomeEvent.ShowSnackbar("${domainProduct.title} added to wishlist"))
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to update wishlist")
                }
            }
        }
    }

    fun claimPromoCode(code: String) {
        viewModelScope.launch {
            val result = saveAdCouponUseCase(code)

            if (result.isSuccess) {
                _events.send(HomeEvent.ShowSnackbar("🎉 Promo Code $code saved! It will be applied at checkout."))
            } else {
                _events.send(HomeEvent.ShowSnackbar("Failed to claim promo code."))
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

//private val staticCategories = listOf(
//    CategoryUiModel("jewelry", "JEWELRY", R.drawable.onboarding_ring),
//    CategoryUiModel("attire", "ATTIRE", R.drawable.onboarding_fabric),
//    CategoryUiModel("scent", "SCENT", R.drawable.ic_logo),
//    CategoryUiModel("home", "HOME", R.drawable.home),
//    CategoryUiModel("gear", "GEAR", R.drawable.onboarding_bag)
//)

private val promos = listOf(
    PromoUiModel(
        id = "1",
        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSgGN2pTfic3uVpilnJ0l0Nj3jox1-ZdSQQ5saYLSRqsg&s=10",
        title = "The Dune Collection",
        ctaText = "CLAIM 10% OFF",
        code = "CO-10"
    ),
    PromoUiModel(
        id = "2",
        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPsNU7_l9Dl9ofPFJoZpKBamesMHiL89MOY3RCN3lE3w&s=10",
        title = "Summer Fragrances",
        ctaText = "CLAIM 20% OFF",
        code = "CO-20"
    ),
    PromoUiModel(
        id = "3",
        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVRKcx2298L0qTDGAE-pWii-7R5pB71D6rPrGX0h-IXg&s=10",
        title = "Royal Oud Series",
        ctaText = "CLAIM 50% OFF",
        code = "CO-50"
    )
)