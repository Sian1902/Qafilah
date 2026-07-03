package com.example.qafilah.features.home.di

import com.example.qafilah.features.home.presentation.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get()
    ) }
}