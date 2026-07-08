package com.example.qafilah.core.di

import com.example.qafilah.core.network.NetworkConnectivityObserver
import com.example.qafilah.core.network.ConnectivityObserver
import com.example.qafilah.core.network.ShopifyClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidContext())
    }

    single(named(ShopifyClient.QUALIFIER_STOREFRONT)) {
        ShopifyClient.storefront
    }

    single(named(ShopifyClient.QUALIFIER_ADMIN)) {
        ShopifyClient.admin
    }

    single { ShopifyClient.storefront }
}