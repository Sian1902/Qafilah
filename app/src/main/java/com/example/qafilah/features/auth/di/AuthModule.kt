package com.example.qafilah.features.auth.di

import com.example.qafilah.features.auth.data.datasource.FirebaseAuthRemoteDataSource
import com.example.qafilah.features.auth.data.datasource.FirebaseAuthRemoteDataSourceImpl
import com.example.qafilah.features.auth.data.repo.AuthRepositoryImpl
import com.example.qafilah.features.auth.data.datasource.ShopifyAuthRemoteDataSource
import com.example.qafilah.features.auth.data.datasource.ShopifyAuthRemoteDataSourceImpl
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.auth.domain.usecase.GetAuthStateUseCase
import com.example.qafilah.features.auth.domain.usecase.SignInUseCase
import com.example.qafilah.features.auth.domain.usecase.SignUpUseCase
import com.example.qafilah.features.auth.presentation.AuthViewModel
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import com.example.qafilah.features.wishlist.presentation.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single { FirebaseAuth.getInstance() }

    single<ShopifyAuthRemoteDataSource> {
        ShopifyAuthRemoteDataSourceImpl(apolloClient = get())
    }

    single<FirebaseAuthRemoteDataSource>{
        FirebaseAuthRemoteDataSourceImpl(firebaseAuth = get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            shopifyDataSource = get(),
            tokenLocalDataSource = get(),
            firebaseDataSource = get()
        )
    }



    factory { SignInUseCase(repository = get()) }

    factory { SignUpUseCase(repository = get()) }

    factory { GetAuthStateUseCase(repository = get()) }



    factory { RequireAuth(getAuthStateUseCase = get()) }
    viewModel {
        AuthViewModel(
            signInUseCase = get(),
            signUpUseCase = get(),
        )
    }

    viewModel { WishlistViewModel(requireAuth = get()) }
}