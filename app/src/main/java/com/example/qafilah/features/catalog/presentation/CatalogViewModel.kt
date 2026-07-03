package com.example.qafilah.features.catalog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.model.StoreCollection
import com.example.qafilah.features.catalog.domain.usecases.*
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getSingleProductUseCase: GetSingleProductUseCase,
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val getCollectionProductsUseCase: GetCollectionProductsUseCase,
    private val isProductWishlistedUseCase: IsProductWishlistedUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase
) : ViewModel() {

    private val _list = MutableStateFlow<List<Product>>(emptyList())
    val list = _list.asStateFlow()

    private val _collections = MutableStateFlow<List<StoreCollection>>(emptyList())
    val collections = _collections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = getCollectionsUseCase(limit = 20, after = null)
                _collections.value = result
            } catch (e: Exception) {
            }
        }
    }

    fun loadCategoryProducts(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _list.value = emptyList()
            try {
                val result = getCollectionProductsUseCase(categoryId)
                _list.value = result.products


                result.products.forEach { product -> observeWishlistState(product.id) }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun observeWishlistState(productId: String) {
        viewModelScope.launch {
            isProductWishlistedUseCase(productId).collect { isWishlisted ->
                _list.value = _list.value.map { product ->
                    product
                }
            }
        }
    }


    fun toggleFavorite(product: Product, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            try {
                if (isCurrentlyFavorite) {
                    removeFromWishlistUseCase(product.id)
                } else {
                    addToWishlistUseCase(
                        productId = product.id,
                        handle = product.id,
                        title = product.title,
                        imageUrl = product.imageUrl,
                        vendor = product.vendor,
                        price = product.priceAmount.toDoubleOrNull() ?: 0.0,
                        currencyCode = product.currencyCode
                    )
                }
            } catch (e: Exception) { }
        }
    }
}