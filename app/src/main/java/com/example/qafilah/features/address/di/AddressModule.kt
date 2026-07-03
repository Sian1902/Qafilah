package com.example.qafilah.features.address.di

import com.example.qafilah.features.address.data.datasource.AddressRemoteDataSource
import com.example.qafilah.features.address.data.datasource.AddressRemoteDataSourceImpl
import com.example.qafilah.features.address.data.repo.AddressRepositoryImpl
import com.example.qafilah.features.address.domain.repository.AddressRepository
import com.example.qafilah.features.address.domain.usecase.CreateAddressUseCase
import com.example.qafilah.features.address.domain.usecase.DeleteAddressUseCase
import com.example.qafilah.features.address.domain.usecase.GetAddressesUseCase
import com.example.qafilah.features.address.domain.usecase.UpdateAddressUseCase
import com.example.qafilah.features.address.presentation.AddressViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addressModule = module {
    single<AddressRemoteDataSource> {
        AddressRemoteDataSourceImpl(apolloClient = get())
    }

    single<AddressRepository> {
        AddressRepositoryImpl(remoteDataSource = get())
    }

    factory { GetAddressesUseCase(repository = get()) }
    factory { CreateAddressUseCase(repository = get()) }
    factory { UpdateAddressUseCase(repository = get()) }
    factory { DeleteAddressUseCase(repository = get()) }

    viewModel {
        AddressViewModel(
            getAddressesUseCase = get(),
            createAddressUseCase = get(),
            updateAddressUseCase = get(),
            deleteAddressUseCase = get(),
            tokenProvider = get()
        )
    }
}
