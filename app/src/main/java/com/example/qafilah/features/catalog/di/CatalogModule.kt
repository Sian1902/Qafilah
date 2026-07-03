package com.example.qafilah.features.catalog.di

import com.example.qafilah.features.catalog.data.datasource.AdsCouponDataStore
import com.example.qafilah.features.catalog.data.datasource.AdsCouponDataStoreImpl
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSource
import com.example.qafilah.features.catalog.data.datasource.CatalogRemoteDataSourceImpl
import com.example.qafilah.features.catalog.data.repo.AdsRepositoryImpl
import com.example.qafilah.features.catalog.data.repo.CatalogRepositoryImpl
import com.example.qafilah.features.catalog.domain.repo.AdsRepository
import com.example.qafilah.features.catalog.domain.repo.CatalogRepository
import com.example.qafilah.features.catalog.domain.usecases.ClearPendingAdCouponUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetBestSellingUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionProductsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetCollectionsUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetPendingAdCouponUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetSingleProductUseCase
import com.example.qafilah.features.catalog.domain.usecases.SaveAdCouponUseCase
import com.example.qafilah.features.catalog.domain.usecases.SearchProductsUseCase
import com.example.qafilah.features.catalog.presentation.CatalogViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val catalogModule = module {
    single<CatalogRemoteDataSource> { CatalogRemoteDataSourceImpl(apolloClient = get()) }
    single<AdsCouponDataStore>{ AdsCouponDataStoreImpl(get()) }

    single<CatalogRepository> {
        CatalogRepositoryImpl(
            remoteDataSource = get()
        )
    }
    single<AdsRepository> { AdsRepositoryImpl(get() ) }

    factory { SearchProductsUseCase(homeRepository = get()) }
    factory { GetSingleProductUseCase(homeRepository = get()) }
    factory { GetBestSellingUseCase(catalogRepository = get()) }
    factory { GetCollectionsUseCase(catalogRepository = get()) }
    factory { GetCollectionProductsUseCase(catalogRepository = get()) }
    factory<GetPendingAdCouponUseCase> { GetPendingAdCouponUseCase(repository = get()) }
    factory<SaveAdCouponUseCase>{ SaveAdCouponUseCase(get()) }
    factory<ClearPendingAdCouponUseCase> { ClearPendingAdCouponUseCase(get()) }



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