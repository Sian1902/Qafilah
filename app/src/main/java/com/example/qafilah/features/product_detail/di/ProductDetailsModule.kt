package com.example.qafilah.features.product_detail.di

import com.example.qafilah.features.product_detail.presentation.viewmodel.ProductDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val productDetailsModule = module {
    viewModel { ProductDetailViewModel(get(), get(), get(), get(), get(), get()) }
}
