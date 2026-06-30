package com.example.qafilah.features.product_detail.di

import com.example.qafilah.features.product_detail.data.datasource.ProductDetailRemoteDataSource
import com.example.qafilah.features.product_detail.data.repo.ProductDetailRepositoryImpl
import com.example.qafilah.features.product_detail.domain.repo.ProductDetailRepository
import com.example.qafilah.features.product_detail.domain.usecase.GetProductDetailUseCase
import com.example.qafilah.features.product_detail.presentation.ProductDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val productDetailModule = module {
    single { ProductDetailRemoteDataSource() }
    single<ProductDetailRepository> { ProductDetailRepositoryImpl(get()) }
    factory { GetProductDetailUseCase(get()) }
    viewModel { ProductDetailViewModel(get()) }
}
