package com.example.qafilah.features.checkout.presentation.address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.usecase.GetAddressesUseCase
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.UpdateBuyerIdentityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutAddressViewModel(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val updateBuyerIdentityUseCase: UpdateBuyerIdentityUseCase,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutAddressUIState())
    val uiState = _uiState.asStateFlow()


    fun loadAddresses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAddresses = true, error = null) }

            val token = tokenProvider.getToken()
            if (token == null) {
                _uiState.update { it.copy(isLoadingAddresses = false, error = "Authentication error. Please log in again.") }
                return@launch
            }

            val result = getAddressesUseCase(token)

            result.onSuccess { addressList ->
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
                        selectedAddressId = newSelectionId
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoadingAddresses = false, error = error.message)
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
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            val result = updateBuyerIdentityUseCase(cartId, selectedAddress)

            _uiState.update { it.copy(isSubmitting = false) }

            result.onSuccess { updatedCart ->
                onSuccess(updatedCart, selectedAddress)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(error = error.message)
                }
            }
        }
    }
}