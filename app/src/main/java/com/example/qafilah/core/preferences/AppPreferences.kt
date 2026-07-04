package com.example.qafilah.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class AppPreferences(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
        private val LANGUAGE_CODE_KEY = stringPreferencesKey("language_code")
        private val CURRENCY_CODE_KEY = stringPreferencesKey("currency_code")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    val themeMode: Flow<ThemeMode> = context.appDataStore.data.map { preferences ->
        val modeIndex = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.ordinal
        ThemeMode.entries[modeIndex]
    }

    val languageCode: Flow<String> = context.appDataStore.data.map { preferences ->
        preferences[LANGUAGE_CODE_KEY] ?: "en"
    }

    val currencyCode: Flow<String> = context.appDataStore.data.map { preferences ->
        preferences[CURRENCY_CODE_KEY] ?: "USD"
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.appDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.ordinal
        }
    }

    suspend fun setLanguageCode(code: String) {
        context.appDataStore.edit { preferences ->
            preferences[LANGUAGE_CODE_KEY] = code
        }
    }

    suspend fun setCurrencyCode(code: String) {
        context.appDataStore.edit { preferences ->
            preferences[CURRENCY_CODE_KEY] = code
        }
    }
}
