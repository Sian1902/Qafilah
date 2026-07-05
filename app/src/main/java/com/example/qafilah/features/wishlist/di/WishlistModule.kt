package com.example.qafilah.features.wishlist.di

import androidx.room.Room
import com.example.qafilah.core.data.AppDatabase
import com.example.qafilah.features.wishlist.data.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.ClearWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.GetWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import com.example.qafilah.features.wishlist.presentation.WishlistViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val wishlistModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "qafilah_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().wishlistDao() }

    single { OkHttpClient() }
    single { WishlistLocalDataSource(get(), androidContext(), get()) }

    factory { GetWishlistUseCase(get(),get()) }
    factory { IsProductWishlistedUseCase(get(),get()) }
    factory { AddToWishlistUseCase(get(),get()) }
    factory { RemoveFromWishlistUseCase(get(),get()) }
    factory { ClearWishlistUseCase(get(),get()) }

    viewModel { WishlistViewModel(get(), get(), get(), get(), get()) }
}