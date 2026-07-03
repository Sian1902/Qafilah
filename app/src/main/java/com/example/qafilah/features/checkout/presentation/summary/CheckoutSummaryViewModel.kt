package com.example.qafilah.features.checkout.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.domain.usecase.UpdateDeliveryOptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutSummaryViewModel(
    private val updateDeliveryOptionUseCase: UpdateDeliveryOptionUseCase
) : ViewModel() {

    private val _isShippingUpdating = MutableStateFlow(false)
    val isShippingUpdating = _isShippingUpdating.asStateFlow()

    private val _selectedShippingHandle = MutableStateFlow<String?>(null)
    val selectedShippingHandle = _selectedShippingHandle.asStateFlow()

    fun selectShippingOption(
        cartId: String,
        groupId: String,
        handle: String,
        onSuccess: (CheckoutCart) -> Unit
    ) {
        _selectedShippingHandle.value = handle
        viewModelScope.launch {
            _isShippingUpdating.value = true
            val result = updateDeliveryOptionUseCase(cartId, groupId, handle)
            _isShippingUpdating.value = false

            result.onSuccess { updatedCart ->
                onSuccess(updatedCart)
            }
        }
    }
}