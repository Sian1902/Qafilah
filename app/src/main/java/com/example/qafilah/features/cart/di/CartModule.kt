package com.example.qafilah.features.cart.di

import com.example.qafilah.features.cart.data.datasource.CartLocalDataSource
import com.example.qafilah.features.cart.data.datasource.CartLocalDataSourceImpl
import com.example.qafilah.features.cart.data.datasource.CartRemoteDataSource
import com.example.qafilah.features.cart.data.datasource.CartRemoteDataSourceImpl
import com.example.qafilah.features.cart.data.repo.CartRepositoryImpl
import com.example.qafilah.features.cart.domain.repo.CartRepository
import com.example.qafilah.features.cart.domain.usecase.AddCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.FetchCartUseCase
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cartModule = module {
    single<CartRepository> { CartRepositoryImpl(get(), get()) }
    single<CartRemoteDataSource> { CartRemoteDataSourceImpl(get()) }
    single<CartLocalDataSource> { CartLocalDataSourceImpl(get()) }

    factory<AddCartItemUseCase> { AddCartItemUseCase(get()) }
    factory<FetchCartUseCase> { FetchCartUseCase(get()) }
    factory<UpdateCartItemQuantityUseCase> { UpdateCartItemQuantityUseCase(get()) }
    factory<RemoveCartItemUseCase> { RemoveCartItemUseCase(get()) }
    factory<ObserveCartStateUseCase> { ObserveCartStateUseCase(get()) }


    viewModel { CartViewModel(
        get(),
        get(),
        get(),
        get(),
        get() )
    }
}