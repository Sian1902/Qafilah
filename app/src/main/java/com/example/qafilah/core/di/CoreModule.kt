package com.example.qafilah.core.di

import com.example.qafilah.MainViewModel
import com.example.qafilah.core.preferences.AppPreferences
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    single { AppPreferences(get()) }
    viewModelOf(::MainViewModel)
}
