package com.example.qafilah.features.product_detail.di

import com.example.qafilah.features.product_detail.presentation.ProductDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val productDetailsModule = module {
    viewModel { ProductDetailViewModel(get()) }
}
