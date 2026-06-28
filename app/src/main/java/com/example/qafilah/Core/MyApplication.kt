package com.example.qafilah.Core
import android.app.Application
import com.example.qafilah.auth.Di.authModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(authModule)
        }
    }
}