package com.example.qafilah.core.application

import android.app.Application
import com.example.qafilah.features.auth.Di.authModule
import com.example.qafilah.core.di.networkModule
import com.example.qafilah.features.catalog.di.catalogModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class QafilahApp : Application() {

    private val modules = listOf(
        networkModule,
        catalogModule,
        authModule
    )

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@QafilahApp)
            modules(modules)
        }
    }
}