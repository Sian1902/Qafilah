package com.example.qafilah.features.orders.di

import com.example.qafilah.features.orders.data.datasource.OrderLocalDataSource
import com.example.qafilah.features.orders.data.repo.OrdersRepositoryImpl
import com.example.qafilah.features.orders.domain.repository.OrdersRepository
import com.example.qafilah.features.orders.domain.usecase.GetOrderByIdUseCase
import com.example.qafilah.features.orders.domain.usecase.GetOrdersUseCase
import com.example.qafilah.features.orders.domain.usecase.RefreshOrdersUseCase
import com.example.qafilah.features.orders.presentation.order_details.OrderDetailsViewModel
import com.example.qafilah.features.orders.presentation.order.OrdersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ordersModule = module {
    single { get<com.example.qafilah.core.data.AppDatabase>().orderDao() }
    single { OrderLocalDataSource(orderDao = get()) }

    single<OrdersRepository> {
        OrdersRepositoryImpl(
            profileRemoteDataSource = get(),
            localDataSource = get()
        )
    }

    factory { GetOrdersUseCase(repository = get()) }
    factory { GetOrderByIdUseCase(repository = get()) }
    factory { RefreshOrdersUseCase(repository = get()) }

    viewModel {
        OrdersViewModel(
            getOrdersUseCase = get(),
            refreshOrdersUseCase = get(),
            convertPriceUseCase = get()
        )
    }

    viewModel {
        OrderDetailsViewModel(
            getOrderByIdUseCase = get(),
            convertPriceUseCase = get()
        )
    }
}
