package com.example.qafilah.features.profile.di

import com.example.qafilah.core.network.ShopifyClient
import com.example.qafilah.core.token.TokenLocalDataSource
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.core.token.TokenProviderImpl
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.profile.data.datasource.ProfileRemoteDataSource
import com.example.qafilah.features.profile.data.datasource.ProfileRemoteDataSourceImpl
import com.example.qafilah.features.profile.data.repo.ProfileRepositoryImpl
import com.example.qafilah.features.profile.domain.repository.ProfileRepository
import com.example.qafilah.features.auth.domain.usecase.SignOutUseCase
import com.example.qafilah.features.profile.domain.usecase.GetCustomerProfileUseCase
import com.example.qafilah.features.profile.domain.usecase.GetPersonalDetailsUseCase
import com.example.qafilah.features.profile.domain.usecase.UpdateProfileUseCase
import com.example.qafilah.features.profile.presentation.editprofile.EditProfileViewModel
import com.example.qafilah.features.profile.presentation.persondetails.PersonalDetailsViewModel
import com.example.qafilah.features.profile.presentation.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val profileModule = module {
    single<TokenProvider> {
        TokenProviderImpl(tokenLocalDataSource = get<TokenLocalDataSource>())
    }

    single<ProfileRemoteDataSource> {
        ProfileRemoteDataSourceImpl(apolloClient = get(named(ShopifyClient.QUALIFIER_STOREFRONT)))
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(
            remoteDataSource = get(),
            authRepository = get<AuthRepository>()
        )
    }

    factory { GetCustomerProfileUseCase(repository = get()) }
    factory { GetPersonalDetailsUseCase(repository = get()) }
    factory { UpdateProfileUseCase(repository = get()) }


    viewModel {
        ProfileViewModel(
            getCustomerProfile = get(),
            signOutUseCase = get(),
            tokenProvider = get(),
            currencyRepository = get()
        )
    }

    viewModel {
        PersonalDetailsViewModel(
            getPersonalDetailsUseCase = get(),
            tokenProvider = get()
        )
    }

    viewModel {
        EditProfileViewModel(
            getPersonalDetailsUseCase = get(),
            updateProfileUseCase = get(),
            tokenProvider = get()
        )
    }
}
