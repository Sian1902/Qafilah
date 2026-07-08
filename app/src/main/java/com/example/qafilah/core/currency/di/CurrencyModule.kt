package com.example.qafilah.core.currency.di

import com.example.qafilah.BuildConfig
import com.example.qafilah.core.currency.ConvertRawPriceUseCase
import com.example.qafilah.core.currency.data.remote.CurrencyService
import com.example.qafilah.core.currency.domain.repo.CurrencyRepository
import com.example.qafilah.core.currency.domain.repo.CurrencyRepositoryImpl
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val currencyModule = module {
    single<CurrencyService> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.CURRENCY_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyService::class.java)
    }

    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get()) }

    factory { ConvertPriceUseCase(get()) }
    factory<ConvertRawPriceUseCase> { ConvertRawPriceUseCase(get()) }
}
