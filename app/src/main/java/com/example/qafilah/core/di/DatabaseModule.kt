package com.example.qafilah.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.qafilah.core.token.CryptoManager
import com.example.qafilah.core.token.TinkCryptoManager
import com.example.qafilah.core.token.TokenLocalDataSource
import com.example.qafilah.core.token.TokenLocalDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val Context.secureDataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_auth_prefs")

val databaseModule = module {
    single<CryptoManager> {
        TinkCryptoManager(androidContext())
    }

    single<DataStore<Preferences>> {
        androidContext().secureDataStore
    }

    single<TokenLocalDataSource> {
        TokenLocalDataSourceImpl(get(), get())
    }
}