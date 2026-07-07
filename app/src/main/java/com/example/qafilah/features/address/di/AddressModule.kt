package com.example.qafilah.features.address.di

import com.example.qafilah.features.address.data.datasource.AddressRemoteDataSource
import com.example.qafilah.features.address.data.datasource.AddressRemoteDataSourceImpl
import com.example.qafilah.features.address.data.datasource.OsmApi
import com.example.qafilah.features.address.data.repo.AddressRepositoryImpl
import com.example.qafilah.features.address.domain.repository.AddressRepository
import com.example.qafilah.features.address.domain.usecase.CreateAddressUseCase
import com.example.qafilah.features.address.domain.usecase.DeleteAddressUseCase
import com.example.qafilah.features.address.domain.usecase.GetAddressesUseCase
import com.example.qafilah.features.address.domain.usecase.SearchAddressUseCase
import com.example.qafilah.features.address.domain.usecase.UpdateAddressUseCase
import com.example.qafilah.features.address.domain.usecase.SetDefaultAddressUseCase
import com.example.qafilah.features.address.presentation.AddressViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val addressModule = module {
    single<AddressRemoteDataSource> {
        AddressRemoteDataSourceImpl(apolloClient = get())
    }

    single<AddressRepository> {
        AddressRepositoryImpl(remoteDataSource = get(),osmApi = get())
    }

    factory { GetAddressesUseCase(repository = get()) }
    factory { CreateAddressUseCase(repository = get()) }
    factory { UpdateAddressUseCase(repository = get()) }
    factory { DeleteAddressUseCase(repository = get()) }
    factory { SetDefaultAddressUseCase(repository = get()) }
    factory { SearchAddressUseCase(get()) }

    viewModel {
        AddressViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<OsmApi> {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OsmApi::class.java)
    }
}
