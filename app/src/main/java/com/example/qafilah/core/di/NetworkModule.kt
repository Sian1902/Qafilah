package com.example.qafilah.core.di

import com.example.qafilah.core.network.ShopifyClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single { ShopifyClient.instance }

}