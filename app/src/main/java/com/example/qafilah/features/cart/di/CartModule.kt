package com.example.qafilah.features.cart.di

import com.example.qafilah.core.network.ShopifyClient
import com.example.qafilah.features.cart.data.datasource.CartLocalDataSource
import com.example.qafilah.features.cart.data.datasource.CartLocalDataSourceImpl
import com.example.qafilah.features.cart.data.datasource.CartRemoteDataSource
import com.example.qafilah.features.cart.data.datasource.CartRemoteDataSourceImpl
import com.example.qafilah.features.cart.data.repo.CartRepositoryImpl
import com.example.qafilah.features.cart.domain.repo.CartRepository
import com.example.qafilah.features.cart.domain.usecase.AddCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.ApplyDiscountUseCase
import com.example.qafilah.features.cart.domain.usecase.ClearCartUseCase
import com.example.qafilah.features.cart.domain.usecase.FetchCartUseCase
import com.example.qafilah.features.cart.domain.usecase.ManageCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.cart.domain.usecase.RemoveDiscountUseCase
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cartModule = module {
    single<CartRepository> { CartRepositoryImpl(get(), get()) }
    single<CartRemoteDataSource> { CartRemoteDataSourceImpl(get(named(ShopifyClient.QUALIFIER_STOREFRONT))) }
    single<CartLocalDataSource> { CartLocalDataSourceImpl(get()) }

    factory<AddCartItemUseCase> { AddCartItemUseCase(get()) }
    factory<FetchCartUseCase> { FetchCartUseCase(get()) }
    factory<ObserveCartStateUseCase> { ObserveCartStateUseCase(get()) }
    factory<ManageCartItemUseCase> { ManageCartItemUseCase(get()) }
    factory<ApplyDiscountUseCase> { ApplyDiscountUseCase(get()) }
    factory<RemoveDiscountUseCase> { RemoveDiscountUseCase(get()) }
    factory<ClearCartUseCase> { ClearCartUseCase(get()) }

    viewModel { CartViewModel(
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get()
        )
    }
}
