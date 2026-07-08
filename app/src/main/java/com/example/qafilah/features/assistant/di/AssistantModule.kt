package com.example.qafilah.features.assistant.di

import com.example.qafilah.core.data.AppDatabase
import com.example.qafilah.features.assistant.data.AssistantRepositoryImpl
import com.example.qafilah.features.assistant.data.remote.AssistantApiService
import com.example.qafilah.features.assistant.data.remote.AssistantRemoteDataSource
import com.example.qafilah.features.assistant.domain.repository.AssistantRepository
import com.example.qafilah.features.assistant.domain.usecase.ClearChatHistoryUseCase
import com.example.qafilah.features.assistant.domain.usecase.GetChatHistoryUseCase
import com.example.qafilah.features.assistant.domain.usecase.SendPromptUseCase
import com.example.qafilah.features.assistant.presentation.AssistantViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val assistantModule = module {
    single { get<AppDatabase>().assistantDao() }

    single<AssistantApiService> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ahmed030223.app.n8n.cloud/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AssistantApiService::class.java)
    }

    single { AssistantRemoteDataSource(apiService = get()) }

    single<AssistantRepository> {
        AssistantRepositoryImpl(
            get(),
            get()
        )
    }

    factory { SendPromptUseCase(get()) }
    factory { GetChatHistoryUseCase(repository = get(), authRepository = get()) }
    factory { ClearChatHistoryUseCase(repository = get(), authRepository = get()) }

    viewModel {
        AssistantViewModel(
            getChatHistoryUseCase = get(),
            sendPromptUseCase = get(),
            clearChatHistoryUseCase = get(),
            getSingleProductUseCase = get(),
            convertPriceUseCase = get()
        )
    }
}