package com.example.qafilah.features.checkout.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.R
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.usecase.GetAddressesUseCase
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.UpdateBuyerIdentityUseCase
import com.example.qafilah.features.profile.domain.usecase.GetPersonalDetailsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutAddressViewModel(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val getPersonalDetailsUseCase: GetPersonalDetailsUseCase,
    private val updateBuyerIdentityUseCase: UpdateBuyerIdentityUseCase,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutAddressUIState())
    val uiState = _uiState.asStateFlow()


    fun loadAddresses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAddresses = true, error = null, errorMessageResId = null) }

            val token = tokenProvider.getToken()
            if (token == null) {
                _uiState.update {
                    it.copy(isLoadingAddresses = false, errorMessageResId = R.string.checkout_auth_error)
                }
                return@launch
            }

            val addressesDeferred = async { getAddressesUseCase(token) }
            val profileDeferred = async { getPersonalDetailsUseCase() }

            val addressesResult = addressesDeferred.await()
            val profileResult = profileDeferred.await()

            val fetchedAppUser = profileResult.getOrNull()

            addressesResult.onSuccess { addressList ->
                val currentState = _uiState.value
                val selectionStillExists = addressList.any { it.id == currentState.selectedAddressId }
                val newSelectionId = if (selectionStillExists) {
                    currentState.selectedAddressId
                } else {
                    addressList.find { it.isDefault }?.id ?: addressList.firstOrNull()?.id
                }

                _uiState.update {
                    it.copy(
                        isLoadingAddresses = false,
                        addresses = addressList,
                        selectedAddressId = newSelectionId,
                        appUser = fetchedAppUser
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoadingAddresses = false, error = error.message, errorMessageResId = null)
                }
            }
        }
    }

    fun selectAddress(addressId: String) {
        _uiState.update { it.copy(selectedAddressId = addressId) }
    }


    fun submitAddress(cartId: String, onSuccess: (CheckoutCart, ShippingAddress) -> Unit) {
        val currentState = _uiState.value
        val selectedAddress = currentState.addresses.find { it.id == currentState.selectedAddressId }

        if (selectedAddress == null) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null, errorMessageResId = null) }

            val result = updateBuyerIdentityUseCase(cartId, selectedAddress)

            _uiState.update { it.copy(isSubmitting = false) }

            result.onSuccess { updatedCart ->
                onSuccess(updatedCart, selectedAddress)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(error = error.message, errorMessageResId = null)
                }
            }
        }
    }
}