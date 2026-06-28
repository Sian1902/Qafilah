package com.example.qafilah.wishlist.di

import com.example.qafilah.core.data.AppDatabase
import com.example.qafilah.wishlist.data.WishlistLocalDataSource
import com.example.qafilah.wishlist.domain.usecase.*
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val wishlistModule = module {
    single { get<AppDatabase>().wishlistDao() }
    single { OkHttpClient() }
    single { WishlistLocalDataSource(get(), androidContext(), get()) }

    factory { GetWishlistUseCase(get()) }
    factory { IsProductWishlistedUseCase(get()) }
    factory { AddToWishlistUseCase(get()) }
    factory { RemoveFromWishlistUseCase(get()) }
    factory { ClearWishlistUseCase(get()) }
}