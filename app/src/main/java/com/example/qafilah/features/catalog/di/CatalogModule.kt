package com.example.qafilah.features.catalog.di

import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSourceImpl
import com.example.qafilah.features.catalog.data.repo.CatalogRepositoryImpl
import com.example.qafilah.features.catalog.domain.CatalogRepository
import com.example.qafilah.features.catalog.domain.usecases.GetProductsUseCase
import com.example.qafilah.features.catalog.presentation.CatalogViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val catalogModule = module {
    single<CatalogRemoteDataSource> { CatalogRemoteDataSourceImpl(apolloClient = get()) }

    single<CatalogRepository> { CatalogRepositoryImpl(remoteDataSource = get()) }

    factory { GetProductsUseCase(homeRepository = get()) }

    viewModel { CatalogViewModel(getProductsUseCase = get()) }
}