package com.example.qafilah.features.auth.Di

import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.auth.data.AuthRepositoryImpl
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.auth.domain.usecase.GetAuthStateUseCase
import com.example.qafilah.features.auth.domain.usecase.SignInUseCase
import com.example.qafilah.features.auth.domain.usecase.SignUpUseCase
import com.example.qafilah.features.auth.presentation.AuthViewModel
import com.example.qafilah.features.cart.presentation.CartViewModel
import com.example.qafilah.features.wishlist.presentation.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single { FirebaseAuth.getInstance() }

    single<AuthRepository> { AuthRepositoryImpl(firebaseAuth = get()) }

    factory { SignInUseCase(repository = get()) }

    factory { SignUpUseCase(repository = get()) }

    factory { GetAuthStateUseCase(repository = get()) }



    factory { RequireAuth(getAuthStateUseCase = get()) }
    viewModel {
        AuthViewModel(
            signInUseCase = get(),
            signUpUseCase = get()
        )
    }

    viewModel { CartViewModel(requireAuth = get()) }
    viewModel { WishlistViewModel(requireAuth = get()) }
}