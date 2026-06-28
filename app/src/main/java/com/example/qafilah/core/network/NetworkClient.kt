package com.example.qafilah.core.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.example.qafilah.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient


object ShopifyClient {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Access-Token", BuildConfig.SHOPIFY_API_KEY)
                .build()
            chain.proceed(request)
        })
        .build()

    val instance: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://mad46-and8.myshopify.com/admin/api/2024-01/graphql.json")
        .okHttpClient(okHttpClient)
        .build()
}