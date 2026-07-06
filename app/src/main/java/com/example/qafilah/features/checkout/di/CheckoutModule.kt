package com.example.qafilah.features.checkout.di

import com.example.qafilah.BuildConfig
import com.example.qafilah.core.currency.ConvertPriceUseCase
import com.example.qafilah.core.network.ShopifyClient
import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSource
import com.example.qafilah.features.checkout.data.datasource.CheckoutRemoteDataSourceImpl
import com.example.qafilah.features.checkout.data.datasource.PaymentRemoteDataSource
import com.example.qafilah.features.checkout.data.datasource.PaymentRemoteDataSourceImpl
import com.example.qafilah.features.checkout.data.remote.paymob.PaymobApi
import com.example.qafilah.features.checkout.data.repo.CheckoutRepositoryImpl
import com.example.qafilah.features.checkout.data.repo.PaymentRepositoryImpl
import com.example.qafilah.features.checkout.domain.repo.CheckoutRepository
import com.example.qafilah.features.checkout.domain.repo.PaymentRepository
import com.example.qafilah.features.checkout.domain.usecase.CompleteOrderUseCase
import com.example.qafilah.features.checkout.domain.usecase.CreateCardPaymentIntentionUseCase
import com.example.qafilah.features.checkout.domain.usecase.UpdateBuyerIdentityUseCase
import com.example.qafilah.features.checkout.domain.usecase.UpdateDeliveryOptionUseCase
import com.example.qafilah.features.checkout.presentation.address.CheckoutAddressViewModel
import com.example.qafilah.features.checkout.presentation.payment.CheckoutPaymentViewModel
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import com.example.qafilah.features.checkout.presentation.summary.CheckoutSummaryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PAYMOB_PAYMENT_METHOD_ID = 5353308

val checkoutModule = module {
    single<CheckoutRemoteDataSource> {
        CheckoutRemoteDataSourceImpl(
            get(named(ShopifyClient.QUALIFIER_STOREFRONT)),
                get(named(ShopifyClient.QUALIFIER_ADMIN))
        )
    }

    single<CheckoutRepository> { CheckoutRepositoryImpl(get()) }

    factory<UpdateBuyerIdentityUseCase> { UpdateBuyerIdentityUseCase(get()) }
    factory<UpdateDeliveryOptionUseCase> { UpdateDeliveryOptionUseCase(get()) }
    factory<CompleteOrderUseCase> { CompleteOrderUseCase(get()) }

    single<PaymobApi> {
        Retrofit.Builder()
            .baseUrl("https://accept.paymob.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymobApi::class.java)
    }

    single<PaymentRemoteDataSource> {
        PaymentRemoteDataSourceImpl(
            paymobApi = get(),
            secretKey = BuildConfig.PAYMOB_SECRET_KEY,
            paymentMethodId = PAYMOB_PAYMENT_METHOD_ID
        )
    }

    single<PaymentRepository> {
        PaymentRepositoryImpl(
            remoteDataSource = get(),
            publicKey = BuildConfig.PAYMOB_PUBLIC_KEY
        )
    }

    factory<CreateCardPaymentIntentionUseCase> { CreateCardPaymentIntentionUseCase(get()) }

    viewModel { CheckoutSharedViewModel(get(), get<ConvertPriceUseCase>()) }
    viewModel { CheckoutSummaryViewModel(get()) }
    viewModel { CheckoutAddressViewModel(get(), get(), get(), get()) }
    viewModel { CheckoutPaymentViewModel(get(), get(), get(), get()) }
}