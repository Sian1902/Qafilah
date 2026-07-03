package com.example.qafilah.features.checkout.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.UpdateDeliveryOptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutSummaryViewModel(
    private val updateDeliveryOptionUseCase: UpdateDeliveryOptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutSummaryUIState())
    val uiState = _uiState.asStateFlow()

    fun selectShippingOption(
        cartId: String,
        groupId: String,
        handle: String,
        onSuccess: (CheckoutCart) -> Unit
    ) {

        val currentState = _uiState.value

        if (currentState.isRecalculating) return

        if (currentState.selectedDeliveryHandle == handle) return

        _uiState.update { currentState ->
            currentState.copy(
                selectedDeliveryHandle = handle,
                isRecalculating = true
            )
        }

        viewModelScope.launch {
            val result = updateDeliveryOptionUseCase(cartId, groupId, handle)

            _uiState.update { currentState ->
                currentState.copy(isRecalculating = false)
            }

            result.onSuccess { updatedCart ->
                onSuccess(updatedCart)
            }
        }
    }
}