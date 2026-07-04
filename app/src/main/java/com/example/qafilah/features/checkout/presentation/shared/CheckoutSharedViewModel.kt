package com.example.qafilah.features.checkout.presentation.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.checkout.data.mapper.toCheckoutCart
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutSharedViewModel(
    private val observeCartStateUseCase: ObserveCartStateUseCase
) : ViewModel() {

    private val _cartState = MutableStateFlow<CheckoutCart?>(null)
    val cartState = _cartState.asStateFlow()

    private val _selectedAddress = MutableStateFlow<ShippingAddress?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    init {
        viewModelScope.launch {
            observeCartStateUseCase().collect { storeCart ->
                _cartState.value = storeCart!!.toCheckoutCart()
            }
        }
    }

    fun updateCartState(newCart: CheckoutCart) {
        _cartState.value = newCart
    }

    fun setShippingAddress(address: ShippingAddress) {
        _selectedAddress.value = address
    }
}