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
import com.example.qafilah.features.address.domain.usecase.SetDefaultAddressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val tokenProvider: TokenProvider,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase
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
            _uiState.value = _uiState.value.copy(isOperationInProgress = true, error = null, operationSuccess = null)
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(isOperationInProgress = false, error = "Not authenticated")
                return@launch
            }

            createAddressUseCase(token, address)
                .onSuccess { createdAddress ->
                    if (address.isDefault) {
                        setDefaultAddressInternal(token, createdAddress.id)
                    } else {
                        refreshAddressesAfterOperation(token)
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isOperationInProgress = false,
                        operationSuccess = false,
                        error = error.message ?: "Unable to create address"
                    )
                }
        }
    }

    fun updateAddress(address: ShippingAddress) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperationInProgress = true, error = null, operationSuccess = null)
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(isOperationInProgress = false, error = "Not authenticated")
                return@launch
            }

            updateAddressUseCase(token, address)
                .onSuccess { updatedAddress ->
                    if (address.isDefault) {
                        setDefaultAddressInternal(token, updatedAddress.id)
                    } else {
                        refreshAddressesAfterOperation(token)
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isOperationInProgress = false,
                        operationSuccess = false,
                        error = error.message ?: "Unable to update address"
                    )
                }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperationInProgress = true, error = null, operationSuccess = null)
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(isOperationInProgress = false, error = "Not authenticated")
                return@launch
            }

            deleteAddressUseCase(token, addressId)
                .onSuccess {
                    refreshAddressesAfterOperation(token)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isOperationInProgress = false,
                        operationSuccess = false,
                        error = error.message ?: "Unable to delete address"
                    )
                }
        }
    }

    fun setDefaultAddress(addressId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperationInProgress = true, error = null, operationSuccess = null)
            val token = tokenProvider.getToken() ?: run {
                _uiState.value = _uiState.value.copy(isOperationInProgress = false, error = "Not authenticated")
                return@launch
            }

            setDefaultAddressInternal(token, addressId)
        }
    }

    fun consumeOperationResult() {
        _uiState.value = _uiState.value.copy(operationSuccess = null, error = null)
    }

    private suspend fun setDefaultAddressInternal(token: String, addressId: String) {
        setDefaultAddressUseCase(token, addressId)
            .onSuccess {
                refreshAddressesAfterOperation(token)
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isOperationInProgress = false,
                    operationSuccess = false,
                    error = error.message ?: "Unable to set default address"
                )
            }
    }

    private suspend fun refreshAddressesAfterOperation(token: String) {
        getAddressesUseCase(token)
            .onSuccess { addresses ->
                _uiState.value = _uiState.value.copy(
                    isOperationInProgress = false,
                    operationSuccess = true,
                    addresses = addresses
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isOperationInProgress = false,
                    operationSuccess = false,
                    error = error.message ?: "Unable to refresh addresses"
                )
            }
    }
}
