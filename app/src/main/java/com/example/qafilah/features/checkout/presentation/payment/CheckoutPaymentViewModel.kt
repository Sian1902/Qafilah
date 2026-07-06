package com.example.qafilah.features.checkout.presentation.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.cart.domain.usecase.ClearCartUseCase
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.CompleteOrderUseCase
import com.example.qafilah.features.checkout.domain.usecase.CreateCardPaymentIntentionUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PaymentMethod {
    CARD,
    COD
}

data class CardPaymentData(
    val publicKey: String,
    val clientSecret: String
)

data class CheckoutPaymentUiState(
    val isProcessing: Boolean = false,
    val selectedMethod: PaymentMethod = PaymentMethod.CARD,
    val isCodAvailable: Boolean = true,
    val codLimit: Double = 1000.0,
    val successOrderId: String? = null,
    val error: String? = null,
    val cardPaymentData: CardPaymentData? = null
)

class CheckoutPaymentViewModel(
    private val completeOrderUseCase: CompleteOrderUseCase,
    private val createCardPaymentIntentionUseCase: CreateCardPaymentIntentionUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutPaymentUiState())
    val uiState = _uiState.asStateFlow()

    fun updateCart(cart: CheckoutCart?) {
        if (cart == null) return
        val total = cart.cost.totalAmount.amount.toDouble()
        val isCodAvailable = total <= _uiState.value.codLimit

        _uiState.update { state ->
            state.copy(
                isCodAvailable = isCodAvailable,
                selectedMethod = if (!isCodAvailable) PaymentMethod.CARD else state.selectedMethod
            )
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        val currentState = _uiState.value
        if (method == PaymentMethod.COD && !currentState.isCodAvailable) return
        _uiState.update { it.copy(selectedMethod = method, error = null) }
    }

    fun initiatePayment(
        cart: CheckoutCart,
        onCodSuccess: () -> Unit
    ) {
        val currentState = _uiState.value

        if (currentState.selectedMethod == PaymentMethod.COD) {
            if (!currentState.isCodAvailable) {
                _uiState.update { it.copy(error = "COD not available for orders above ${currentState.codLimit} EGP") }
                return
            }
            _uiState.update { it.copy(isProcessing = true, error = null) }
            viewModelScope.launch {
                _uiState.update { it.copy(isProcessing = false) }
                onCodSuccess()
            }
            return
        }

        _uiState.update { it.copy(isProcessing = true, error = null, cardPaymentData = null) }

        viewModelScope.launch {
            try {
                val intention = createCardPaymentIntentionUseCase(cart)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        cardPaymentData = CardPaymentData(intention.publicKey, intention.clientSecret)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = "Payment initiation failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun onPaymentComplete(success: Boolean, errorMessage: String? = null) {
        _uiState.update { state ->
            state.copy(
                isProcessing = false,
                error = if (!success) (errorMessage ?: "Payment failed. Please try again.") else null
            )
        }
    }

    fun clearCardPaymentData() {
        _uiState.update { it.copy(cardPaymentData = null) }
    }

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

                clearCartUseCase()


                _uiState.update { it.copy(isProcessing = false, successOrderId = orderId) }
            }.onFailure { error ->
                _uiState.update { it.copy(isProcessing = false, error = error.message) }
            }
        }
    }
}