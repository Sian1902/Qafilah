package com.example.qafilah.features.auth.Di

import com.example.qafilah.features.auth.data.AuthRepositoryImpl
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.auth.domain.usecase.SignInUseCase
import com.example.qafilah.features.auth.domain.usecase.SignUpUseCase
import com.example.qafilah.features.auth.presentation.AuthViewModel
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

    factory { SignUpUseCase(repository = get()) }

    // 4. Provide ViewModel
    viewModel {
        AuthViewModel(signInUseCase = get(),
            signUpUseCase = get()
        )
    }
}