package com.example.qafilah.core.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.example.qafilah.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ShopifyClient {

    const val QUALIFIER_STOREFRONT = "storefront_client"
    const val QUALIFIER_ADMIN = "admin_client"

    var currentLanguage: String = "ar"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val storefrontOkHttp = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Storefront-Access-Token", BuildConfig.SHOPIFY_API_KEY)
                .addHeader("Accept-Language", currentLanguage)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val storefront: ApolloClient = ApolloClient.Builder()
        .serverUrl(BuildConfig.STOREFRONT_ENDPOINT)
        .okHttpClient(storefrontOkHttp)
        .build()

    private val adminOkHttp = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Access-Token", BuildConfig.ADMIN_API_KEY)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val admin: ApolloClient = ApolloClient.Builder()
        .serverUrl(BuildConfig.ADMIN_ENDPOINT)
        .okHttpClient(adminOkHttp)
        .build()
}