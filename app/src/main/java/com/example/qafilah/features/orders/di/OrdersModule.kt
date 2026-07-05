package com.example.qafilah.features.orders.di

import com.example.qafilah.features.orders.data.repo.OrdersRepositoryImpl
import com.example.qafilah.features.orders.domain.repository.OrdersRepository
import com.example.qafilah.features.orders.domain.usecase.GetOrderByIdUseCase
import com.example.qafilah.features.orders.domain.usecase.GetOrdersUseCase
import com.example.qafilah.features.orders.presentation.order_details.OrderDetailsViewModel
import com.example.qafilah.features.orders.presentation.order.OrdersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ordersModule = module {
    single<OrdersRepository> {
        OrdersRepositoryImpl(
            profileRemoteDataSource = get()
        )
    }

    factory { GetOrdersUseCase(repository = get()) }
    factory { GetOrderByIdUseCase(repository = get()) }

    viewModel {
        OrdersViewModel(
            getOrdersUseCase = get(),
            tokenProvider = get(),
            convertPriceUseCase = get()
        )
    }

    viewModel {
        OrderDetailsViewModel(
            getOrderByIdUseCase = get(),
            tokenProvider = get(),
            convertPriceUseCase = get()
        )
    }
}
