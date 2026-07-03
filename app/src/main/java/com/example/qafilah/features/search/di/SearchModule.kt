package com.example.qafilah.di

import android.content.Context
import android.content.SharedPreferences
import com.example.qafilah.features.search.data.SearchLocalDataSourceImpl
import com.example.qafilah.features.search.data.datasource.SearchLocalDataSource
import com.example.qafilah.features.search.presentation.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("qafilah_search_prefs", Context.MODE_PRIVATE)
    }

    single<SearchLocalDataSource> { SearchLocalDataSourceImpl(get()) }

    viewModel { SearchViewModel(get(), get(), get(), get(), get(),
        get(), get(), get(), get(), get()) }
}