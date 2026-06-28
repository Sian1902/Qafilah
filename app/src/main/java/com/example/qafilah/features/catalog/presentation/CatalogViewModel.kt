package com.example.qafilah.features.catalog.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionProductsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.SearchProductsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getSingleProductUseCase: GetSingleProductUseCase,
    private val getBestSellingUseCase: GetBestSellingUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val getCollectionProductsUseCase: GetCollectionProductsUseCase
) : ViewModel() {

    private val _list = MutableStateFlow<List<Product>>(emptyList())
    val list = _list.asStateFlow()

    init {
        testDataPipelines()
    }

    private fun testDataPipelines() {
        viewModelScope.launch {
            try {
                val bestSellers = getBestSellingUseCase(limit = 5, after = null)
                Log.e("ShopifyTest", "🔥 Best Sellers Count: ${bestSellers.size}")
                if (bestSellers.isNotEmpty()) {
                    Log.e("ShopifyTest", "   Top Item: ${bestSellers.first().title}")
                }

                val collections = getCollectionsUseCase(limit = 5, after = null)
                Log.e("ShopifyTest", "📁 Collections Count: ${collections.size}")
                if (collections.isNotEmpty()) {
                    Log.e("ShopifyTest", "   First Collection: ${collections.first().title} (ID: ${collections.first().id})")
                }

                val targetCollectionId = "gid://shopify/Collection/305504583757"
                val collectionDetails = getCollectionProductsUseCase(id = targetCollectionId)

                Log.e("ShopifyTest", "📦 Target Collection: ${collectionDetails.collectionInfo.title}")
                Log.e("ShopifyTest", "   Products Inside: ${collectionDetails.products.size}")
                if (collectionDetails.products.isNotEmpty()) {
                    Log.e("ShopifyTest", "   First Product: ${collectionDetails.products.first().title}")
                }

            } catch (e: Exception) {
                Log.e("ShopifyTest", "❌ Network or GraphQL Error: ${e.message}")
            }
        }
    }
}