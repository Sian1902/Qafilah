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



}