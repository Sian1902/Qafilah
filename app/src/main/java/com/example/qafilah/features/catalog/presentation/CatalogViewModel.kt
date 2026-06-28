package com.example.qafilah.features.catalog.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.model.Product
import com.example.qafilah.core.network.ShopifyClient
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSourceImpl
import com.example.qafilah.features.catalog.data.repo.HomeRepositoryImpl
import com.example.qafilah.features.catalog.domain.usecases.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
): ViewModel() {
    private val getProductsUseCase: GetProductsUseCase = GetProductsUseCase(
        homeRepository = HomeRepositoryImpl(
            remoteDataSource = CatalogRemoteDataSourceImpl(
                apolloClient = ShopifyClient.instance
            )
        )
    )

    init {
        getProducts()
    }

    var _list = MutableStateFlow<List<Product>>(emptyList())
    val list = _list.asStateFlow()

    fun getProducts(){
        viewModelScope.launch {
            val response = getProductsUseCase("sh", 20)
            _list.value = response.getOrDefault(emptyList())
            Log.e("ShopifyTest", "getProducts: ${response.getOrDefault(emptyList()).size}")
        }
    }
}