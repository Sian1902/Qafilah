package com.example.qafilah.core.currency

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val currencyModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyService::class.java)
    }

    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get()) }

    factory { ConvertPriceUseCase(get()) }
    factory { ConvertRawPriceUseCase(get()) }
}
