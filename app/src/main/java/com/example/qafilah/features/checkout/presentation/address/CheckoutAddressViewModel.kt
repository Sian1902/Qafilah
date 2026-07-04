package com.example.qafilah.features.checkout.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.UpdateBuyerIdentityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutAddressViewModel(
    private val updateBuyerIdentityUseCase: UpdateBuyerIdentityUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun submitAddress(cartId: String, onSuccess: (CheckoutCart) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = updateBuyerIdentityUseCase(cartId, "MockAddressData")
            _isLoading.value = false

            result.onSuccess { updatedCart ->
                onSuccess(updatedCart)
            }
        }
    }
}