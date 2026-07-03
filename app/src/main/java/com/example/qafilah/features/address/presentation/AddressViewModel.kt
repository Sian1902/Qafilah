package com.example.qafilah.features.address.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.model.ShippingAddressesUiState
import com.example.qafilah.features.address.domain.usecase.CreateAddressUseCase
import com.example.qafilah.features.address.domain.usecase.DeleteAddressUseCase
import com.example.qafilah.features.address.domain.usecase.GetAddressesUseCase
import com.example.qafilah.features.address.domain.usecase.UpdateAddressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShippingAddressesUiState())
    val uiState: StateFlow<ShippingAddressesUiState> = _uiState

    fun loadAddresses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Not authenticated")
                return@launch
            }

            getAddressesUseCase(token)
                .onSuccess { addresses ->
                    _uiState.value = _uiState.value.copy(isLoading = false, addresses = addresses)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message ?: "Unknown error")
                }
        }
    }

    fun createAddress(address: ShippingAddress) {
        viewModelScope.launch {
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(error = "Not authenticated")
                return@launch
            }

            createAddressUseCase(token, address)
                .onSuccess { createdAddress ->
                    _uiState.value = _uiState.value.copy(
                        addresses = _uiState.value.addresses + createdAddress,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message ?: "Unable to create address")
                }
        }
    }

    fun updateAddress(address: ShippingAddress) {
        viewModelScope.launch {
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(error = "Not authenticated")
                return@launch
            }

            updateAddressUseCase(token, address)
                .onSuccess { updatedAddress ->
                    _uiState.value = _uiState.value.copy(
                        addresses = _uiState.value.addresses.map { existing ->
                            if (existing.id == updatedAddress.id) updatedAddress else existing
                        },
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message ?: "Unable to update address")
                }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(error = "Not authenticated")
                return@launch
            }

            deleteAddressUseCase(token, addressId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        addresses = _uiState.value.addresses.filterNot { it.id == addressId },
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message ?: "Unable to delete address")
                }
        }
    }
}
