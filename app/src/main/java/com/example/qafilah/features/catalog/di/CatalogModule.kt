package com.example.qafilah.features.catalog.di

import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSourceImpl
import com.example.qafilah.features.catalog.data.repo.CatalogRepositoryImpl
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionProductsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetProductTypesUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetProductsByTypeUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.qafilah.features.catalog.domain.usecases.SearchProductsUseCase
import com.example.qafilah.features.catalog.presentation.CatalogViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val catalogModule = module {
    single<CatalogRemoteDataSource> { CatalogRemoteDataSourceImpl(apolloClient = get()) }

    single<CatalogRepository> {
        CatalogRepositoryImpl(
            remoteDataSource = get()
        )
    }

    factory { SearchProductsUseCase(homeRepository = get()) }
    factory { GetSingleProductUseCase(homeRepository = get()) }
    factory { GetBestSellingUseCase(catalogRepository = get()) }
    factory { GetCollectionsUseCase(catalogRepository = get()) }
    factory { GetCollectionProductsUseCase(catalogRepository = get()) }
    factory { GetProductTypesUseCase(catalogRepository = get()) }
    factory { GetProductsByTypeUseCase(catalogRepository = get()) }


    viewModel {
        CatalogViewModel(
            searchProductsUseCase = get(),
            getSingleProductUseCase = get(),
            getBestSellingUseCase = get(),
            getCollectionsUseCase = get(),
            getCollectionProductsUseCase = get()
        )
    }
}