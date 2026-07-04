package com.example.qafilah.features.checkout.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.CompleteOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutPaymentUiState(
    val isProcessing: Boolean = false,
    val successOrderId: String? = null,
    val error: String? = null
)

class CheckoutPaymentViewModel(
    private val completeOrderUseCase: CompleteOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutPaymentUiState())
    val uiState = _uiState.asStateFlow()

    fun finalizeOrder(
        cart: CheckoutCart,
        user: AppUser,
        address: ShippingAddress,
        selectedDeliveryHandle: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }

            val result = completeOrderUseCase(cart, user, address, selectedDeliveryHandle)

            result.onSuccess { orderId ->
                _uiState.update { it.copy(isProcessing = false, successOrderId = orderId) }
            }.onFailure { error ->
                _uiState.update { it.copy(isProcessing = false, error = error.message) }
            }
        }
    }
}