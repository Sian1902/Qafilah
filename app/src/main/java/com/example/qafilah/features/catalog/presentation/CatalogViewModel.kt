package com.example.qafilah.features.catalog.presentation

import androidx.lifecycle.ViewModel
import com.example.qafilah.core.model.Product
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionProductsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.qafilah.features.catalog.domain.usecases.SearchProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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