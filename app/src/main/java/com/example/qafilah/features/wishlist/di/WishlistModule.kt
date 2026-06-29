package com.example.qafilah.features.wishlist.di

import com.example.qafilah.core.data.AppDatabase
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.ClearWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.GetWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
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