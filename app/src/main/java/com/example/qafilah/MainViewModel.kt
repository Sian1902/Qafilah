package com.example.qafilah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.network.ShopifyClient
import com.example.qafilah.core.preferences.AppPreferences
import com.example.qafilah.core.preferences.ThemeMode
import com.example.qafilah.core.token.TokenProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class InitialDestination {
    object Splash : InitialDestination()
    object Onboarding : InitialDestination()
    object Login : InitialDestination()
    object Home : InitialDestination()
}

data class AppState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageCode: String = "en",
    val onboardingCompleted: Boolean = false,
    val initialDestination: InitialDestination = InitialDestination.Splash
)

class MainViewModel(
    private val appPreferences: AppPreferences,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    init {
        viewModelScope.launch {
            appPreferences.languageCode.collectLatest { lang ->
                ShopifyClient.currentLanguage = lang
            }
        }
    }

    val appState: StateFlow<AppState> = combine(
        appPreferences.themeMode,
        appPreferences.languageCode,
        appPreferences.isOnboardingCompleted
    ) { theme, lang, onboarding ->
        val isLoggedIn = tokenProvider.getToken() != null

        val destination = when {
            !onboarding -> InitialDestination.Onboarding
            !isLoggedIn -> InitialDestination.Login
            else -> InitialDestination.Home
        }

        AppState(
            themeMode = theme,
            languageCode = lang,
            onboardingCompleted = onboarding,
            initialDestination = destination
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppState()
    )

    fun setLanguage(code: String) {
        viewModelScope.launch {
            appPreferences.setLanguageCode(code)
        }
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            appPreferences.setThemeMode(mode)
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            appPreferences.setOnboardingCompleted(true)
        }
    }
}
