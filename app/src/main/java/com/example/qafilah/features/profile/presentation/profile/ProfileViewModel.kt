package com.example.qafilah.features.profile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.core.currency.domain.repo.CurrencyRepository
import com.example.qafilah.features.auth.domain.usecase.SignOutUseCase
import com.example.qafilah.features.cart.domain.usecase.ClearCartUseCase
import com.example.qafilah.features.profile.domain.usecase.GetCustomerProfileUseCase
import com.example.qafilah.features.wishlist.domain.usecase.GetWishlistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val requireAuth: RequireAuth,
    private val getCustomerProfile: GetCustomerProfileUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        observeCurrency()
        loadAvailableCurrencies()
        observeWishlist()
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlistUseCase().collect { items ->
                _state.update { it.copy(wishlistCount = items.size) }
            }
        }
    }

    private fun observeCurrency() {
        viewModelScope.launch {
            currencyRepository.getSelectedCurrency().collect { code ->
                _state.update { it.copy(selectedCurrency = code) }
            }
        }
    }

    private fun loadAvailableCurrencies() {
        viewModelScope.launch {
            val currencies = currencyRepository.getSupportedCurrencies()
            _state.update { it.copy(availableCurrencies = currencies) }
        }
    }

    fun onCurrencySelected(code: String) {
        viewModelScope.launch {
            currencyRepository.setSelectedCurrency(code)
        }
    }

    fun loadProfile(unknownErrorMessage: String) {
        requireAuth.invoke(
            onAuthenticated = {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true, error = null, showLoginPrompt = false) }

                    getCustomerProfile()
                        .onSuccess { profile ->
                            _state.update { it.copy(isLoading = false, profile = profile) }
                        }
                        .onFailure { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: unknownErrorMessage,
                                    isAuthError = error.message?.contains(
                                        "authenticated",
                                        ignoreCase = true
                                    ) == true
                                )
                            }
                        }
                }
            },
            onGuest = {
                _state.update { it.copy(isLoading = false, showLoginPrompt = true) }
            }
        )
    }

    fun dismissLoginPrompt() {
        _state.update { it.copy(showLoginPrompt = false) }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            clearCartUseCase()
            onComplete()
        }
    }
}
