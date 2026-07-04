package com.example.qafilah.features.checkout.di

import com.example.qafilah.core.network.ShopifyClient
import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSource
import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSourceImpl
import com.example.qafilah.features.checkout.data.repo.CheckoutRepositoryImpl
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository
import com.example.qafilah.features.checkout.domain.usecase.UpdateBuyerIdentityUseCase
import com.example.qafilah.features.checkout.domain.usecase.UpdateDeliveryOptionUseCase
import com.example.qafilah.features.checkout.presentation.address.CheckoutAddressViewModel
import com.example.qafilah.features.checkout.presentation.payment.CheckoutPaymentViewModel
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import com.example.qafilah.features.checkout.presentation.summary.CheckoutSummaryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val checkoutModule = module {
    single<CheckoutRemoteDataSource> {
        CheckoutRemoteDataSourceImpl(get(named(ShopifyClient.QUALIFIER_STOREFRONT)))
    }

    single<CheckoutRepository> { CheckoutRepositoryImpl(get()) }

    factory<UpdateBuyerIdentityUseCase> { UpdateBuyerIdentityUseCase(get()) }
    factory<UpdateDeliveryOptionUseCase> { UpdateDeliveryOptionUseCase(get()) }

    viewModel { CheckoutSharedViewModel(get()) }
    viewModel { CheckoutSummaryViewModel(get()) }
    viewModel { CheckoutAddressViewModel(get(), get(), get()) }
    viewModel { CheckoutPaymentViewModel() }
}