package com.example.qafilah.features.checkout.presentation.shared

import androidx.lifecycle.ViewModel
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckoutSharedViewModel : ViewModel() {

    private val _cartState = MutableStateFlow<CheckoutCart>(CheckoutMocks.initialCart)
    val cartState = _cartState.asStateFlow()

    fun updateCartState(newCart: CheckoutCart) {
        _cartState.value = newCart
    }
}