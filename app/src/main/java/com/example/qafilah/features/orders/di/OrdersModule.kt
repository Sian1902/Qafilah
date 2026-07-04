package com.example.qafilah.features.orders.di

import com.example.qafilah.features.orders.presentation.OrdersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ordersModule = module {
    viewModel {
        OrdersViewModel(
            tokenProvider = get(),
            convertPriceUseCase = get()
        )
    }
}
