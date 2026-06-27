package com.example.qafilah.auth.Di

import com.example.qafilah.auth.data.AuthRepositoryImpl
import com.example.qafilah.auth.domain.repository.AuthRepository
import com.example.qafilah.auth.domain.usecase.SignInUseCase
import com.example.qafilah.auth.presentation.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    // 1. Provide external Firebase instance
    single { FirebaseAuth.getInstance() }

    // 2. Bind Data implementation to Domain interface
    single<AuthRepository> { AuthRepositoryImpl(firebaseAuth = get()) }

    // 3. Provide Use Cases
    factory { SignInUseCase(repository = get()) }

    // 4. Provide ViewModel
    viewModel { AuthViewModel(signInUseCase = get()) }
}