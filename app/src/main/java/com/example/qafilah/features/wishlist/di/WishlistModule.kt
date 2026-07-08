package com.example.qafilah.features.wishlist.di

import androidx.room.Room
import com.example.qafilah.core.data.AppDatabase
import com.example.qafilah.features.wishlist.data.local.WishlistLocalDataSource
import com.example.qafilah.features.wishlist.data.remote.WishlistRemoteDataSource
import com.example.qafilah.features.wishlist.data.remote.WishlistRemoteDataSourceImpl
import com.example.qafilah.features.wishlist.data.repository.WishlistRepositoryImpl
import com.example.qafilah.features.wishlist.domain.repository.WishlistRepository
import com.example.qafilah.features.wishlist.domain.usecase.AddToWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.ClearWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.GetWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.IsProductWishlistedUseCase
import com.example.qafilah.features.wishlist.domain.usecase.RemoveFromWishlistUseCase
import com.example.qafilah.features.wishlist.domain.usecase.SyncWishlistUseCase
import com.example.qafilah.features.wishlist.presentation.WishlistViewModel
import com.google.firebase.database.FirebaseDatabase
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
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

    // --- Firebase & Repository ---
    single {
        FirebaseDatabase.getInstance("https://qafilah-f25f1-default-rtdb.europe-west1.firebasedatabase.app")            // Tells Firebase to cache data offline and sync automatically when internet returns
    }
    single<WishlistRemoteDataSource> { WishlistRemoteDataSourceImpl(get()) }
    // get() automatically resolves LocalDataSource, RemoteDataSource, and AuthDataSource
    single<WishlistRepository> { WishlistRepositoryImpl(get(), get(), get()) }

    // --- Use Cases ---
    factory { GetWishlistUseCase(get()) }
    factory { IsProductWishlistedUseCase(get()) }
    factory { AddToWishlistUseCase(get()) }
    factory { RemoveFromWishlistUseCase(get()) }
    factory { ClearWishlistUseCase(get()) }
    factory { SyncWishlistUseCase(get()) }

    // Note the added 'get()' parameter for SyncWishlistUseCase
    viewModel { WishlistViewModel(get(), get(), get(), get(), get()) }
}