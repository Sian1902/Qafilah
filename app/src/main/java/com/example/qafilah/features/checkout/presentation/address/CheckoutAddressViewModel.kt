package com.example.qafilah.features.checkout.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenProvider
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

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAddresses = true, error = null) }

            val result = getAddressesUseCase(tokenProvider.getToken()!!)

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

    fun submitAddress(cartId: String, onSuccess: (CheckoutCart) -> Unit) {
        val currentState = _uiState.value
        val selectedAddress = currentState.addresses.find { it.id == currentState.selectedAddressId }

        if (selectedAddress == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val result = updateBuyerIdentityUseCase(cartId, selectedAddress)

            _uiState.update { it.copy(isSubmitting = false) }

            result.onSuccess { updatedCart ->
                onSuccess(updatedCart)
            }
        }
    }
}