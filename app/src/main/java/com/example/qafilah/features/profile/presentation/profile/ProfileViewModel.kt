package com.example.qafilah.features.profile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.core.currency.CurrencyMetadata
import com.example.qafilah.core.currency.CurrencyRepository
import com.example.qafilah.features.profile.domain.usecase.GetCustomerProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCustomerProfile: GetCustomerProfileUseCase,
    private val tokenProvider: TokenProvider,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _availableCurrencies = MutableStateFlow<List<CurrencyMetadata>>(emptyList())
    val availableCurrencies: StateFlow<List<CurrencyMetadata>> = _availableCurrencies.asStateFlow()

    init {
        observeCurrency()
        loadAvailableCurrencies()
    }

    private fun observeCurrency() {
        viewModelScope.launch {
            currencyRepository.getSelectedCurrency().collect {
                _selectedCurrency.value = it
            }
        }
    }

    private fun loadAvailableCurrencies() {
        viewModelScope.launch {
            _availableCurrencies.value = currencyRepository.getSupportedCurrencies()
        }
    }

    fun onCurrencySelected(code: String) {
        viewModelScope.launch {
            currencyRepository.setSelectedCurrency(code)
        }
    }

    fun loadProfile(notAuthenticatedMessage: String, unknownErrorMessage: String) {
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            val token = tokenProvider.getToken()
            if (token == null) {
                _state.value = ProfileUiState.Error(notAuthenticatedMessage, isAuthError = true)
                return@launch
            }

            getCustomerProfile(token)
                .onSuccess { profile ->
                    _state.value = ProfileUiState.Success(profile)
                }
                .onFailure { error ->
                    _state.value = ProfileUiState.Error(
                        error.message ?: unknownErrorMessage,
                        isAuthError = false
                    )
                }
        }
    }
}
