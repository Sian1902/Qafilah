package com.example.qafilah.core.application

import android.app.Application
import com.example.qafilah.core.currency.currencyModule
import com.example.qafilah.core.di.coreModule
import com.example.qafilah.core.di.databaseModule
import com.example.qafilah.core.di.networkModule
import com.example.qafilah.di.searchModule
import com.example.qafilah.features.address.di.addressModule
import com.example.qafilah.features.assistant.di.assistantModule
import com.example.qafilah.features.auth.di.authModule
import com.example.qafilah.features.cart.di.cartModule
import com.example.qafilah.features.catalog.di.catalogModule
import com.example.qafilah.features.checkout.di.checkoutModule
import com.example.qafilah.features.home.di.homeModule
import com.example.qafilah.features.product_detail.di.productDetailsModule
import com.example.qafilah.features.profile.di.profileModule
import com.example.qafilah.features.wishlist.di.wishlistModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class QafilahApp : Application() {

    private val modules = listOf(
        coreModule,
        networkModule,
        catalogModule,
        authModule,
        homeModule,
        productDetailsModule,
        profileModule,
        addressModule,
        wishlistModule,
        databaseModule,
        cartModule,
        searchModule,
        checkoutModule,
        currencyModule,
        assistantModule
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