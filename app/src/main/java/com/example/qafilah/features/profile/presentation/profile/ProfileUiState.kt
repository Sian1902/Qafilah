package com.example.qafilah.features.profile.presentation.profile

import com.example.qafilah.core.currency.domain.model.CurrencyMetadata
import com.example.qafilah.features.profile.domain.model.CustomerProfile

data class ProfileUiState(
    val profile: CustomerProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isAuthError: Boolean = false,
    val selectedCurrency: String = "USD",
    val availableCurrencies: List<CurrencyMetadata> = emptyList()
)

