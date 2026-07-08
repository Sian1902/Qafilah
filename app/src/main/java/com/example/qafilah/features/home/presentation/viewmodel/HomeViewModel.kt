package com.example.qafilah.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.R
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetProductTypesUseCase
import com.example.qafilah.features.catalog.domain.usecases.SaveAdCouponUseCase
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistParams
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
private const val CATEGORIES_LIMIT = 10

sealed interface HomeEvent {
    data class ShowToast(val message: String) : HomeEvent
}

class HomeViewModel(
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val getProductTypesUseCase: GetProductTypesUseCase,
    private val isProductWishlistedUseCase: IsProductWishlistedUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase,
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

    fun loadHome(fallbackErrorMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val products = getBestSellingUseCase(limit = PRODUCTS_LIMIT, after = null)
                val collections = getCollectionsUseCase(limit = COLLECTIONS_LIMIT, after = null)
                val productTypes = getProductTypesUseCase(limit = CATEGORIES_LIMIT)
                domainProductsCache = products

                val uiProducts = products.map { product ->
                    val convertedPrice = convertPriceUseCase(product.priceAmount.toDoubleOrNull() ?: 0.0)
                    product.toUiModel(convertedPrice)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = staticPopularBrands,

                        brands = productTypes.map { type -> type.uppercase() },

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
        fallbackErrorMessage: String,
        notLoggedInMessage: String
    ) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null) {
            _events.trySend(HomeEvent.ShowToast(notLoggedInMessage))
            return
        }

        val uiProduct = _uiState.value.products.find { it.id == productId } ?: return
        val domainProduct = domainProductsCache.find { it.id == productId } ?: return
        val wasFavorite = uiProduct.isFavorite

        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    removeFromWishlistUseCase(productId)
                } else {
                    addToWishlistUseCase(
                        AddToWishlistParams(
                            productId = domainProduct.id,
                            handle = domainProduct.id,
                            title = domainProduct.title,
                            imageUrl = domainProduct.imageUrl,
                            vendor = domainProduct.vendor,
                            price = domainProduct.priceAmount.toDoubleOrNull() ?: 0.0,
                            currencyCode = "USD"
                        )
                    )
                    _events.trySend(
                        HomeEvent.ShowToast(
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

    fun claimPromoCode(code: String) {
        viewModelScope.launch {
            val result = saveAdCouponUseCase(code)

            if (result.isSuccess) {
                _events.send(HomeEvent.ShowToast("🎉 Promo Code $code saved! It will be applied at checkout."))
            } else {
                _events.send(HomeEvent.ShowToast("Failed to claim promo code."))
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

private val staticPopularBrands = listOf(
    CategoryUiModel(
        id = "brand_nike",
        label = "NIKE",
        icon = R.drawable.ic_logo,
        imageUrl = "https://nmp.about.nike.com/originals/about/prod/cf68f541-fc92-4373-91cb-086ae0fe2f88/002-nike-logos-swoosh-white.jpg?s=0d91b6d512f3b96eb15376bfb84bd503140904583667147c21ca3d6c4594e7d2"
    ),
    CategoryUiModel(
        id = "brand_vans",
        label = "VANS",
        icon = R.drawable.ic_logo,
        imageUrl = "https://i.pinimg.com/736x/dc/03/89/dc03893a0baf50b2efbdede6350e4d67.jpg"
    ),
    CategoryUiModel(
        id = "brand_palladium",
        label = "PALLADIUM",
        icon = R.drawable.ic_logo,
        imageUrl = "https://i.pinimg.com/736x/7e/52/0a/7e520a5389dfcb4e57009bf207c85d65.jpg"
    ),
    CategoryUiModel(
        id = "brand_adidas",
        label = "ADIDAS",
        icon = R.drawable.ic_logo,
        imageUrl = "https://preview.thenewsmarket.com/Previews/ADID/StillAssets/1920x1440/689347.jpg"
    ),
    CategoryUiModel(
        id = "brand_hydrogen",
        label = "HYDROGEN",
        icon = R.drawable.ic_logo,
        imageUrl = "https://ui-avatars.com/api/?name=Hydrogen&background=111111&color=fff&size=256"
    ),
)