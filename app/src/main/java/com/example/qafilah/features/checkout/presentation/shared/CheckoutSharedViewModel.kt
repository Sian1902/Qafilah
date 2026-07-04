package com.example.qafilah.features.checkout.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.ConvertPriceUseCase
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.checkout.data.mapper.toCheckoutCart
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.cart.domain.model.StoreCart
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutSharedViewModel(
    private val observeCartStateUseCase: ObserveCartStateUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _cartState = MutableStateFlow<CheckoutCart?>(null)
    val cartState = _cartState.asStateFlow()

    private val _displayCartState = MutableStateFlow<CheckoutDisplayCart?>(null)
    val displayCartState = _displayCartState.asStateFlow()

    private val _selectedAddress = MutableStateFlow<ShippingAddress?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _appUser = MutableStateFlow<AppUser?>(null)
    val appUser = _appUser.asStateFlow()

    private val _selectedDeliveryHandle = MutableStateFlow<String?>(null)
    val selectedDeliveryHandle = _selectedDeliveryHandle.asStateFlow()

    init {
        viewModelScope.launch {
            observeCartStateUseCase().collect { storeCart ->
                emitCart(storeCart)
            }
        }
    }

    fun updateCartState(newCart: CheckoutCart) {
        _cartState.value = newCart
        viewModelScope.launch {
            _displayCartState.value = newCart.toDisplayCart(selectedDeliveryHandle = null)
        }
    }

    fun updateCartStateWithSelectedShipping(newCart: CheckoutCart, selectedDeliveryHandle: String?) {
        _cartState.value = newCart
        _selectedDeliveryHandle.value = selectedDeliveryHandle

        viewModelScope.launch {
            _displayCartState.value = newCart.toDisplayCart(selectedDeliveryHandle)
        }
    }

    fun setShippingAddress(address: ShippingAddress) {
        _selectedAddress.value = address
    }

    fun setCustomerProfile(user: AppUser) {
        _appUser.value = user
    }

    private suspend fun emitCart(storeCart: StoreCart?) {
        if (storeCart == null) {
            _cartState.value = null
            _displayCartState.value = null
            return
        }

        val checkoutCart = storeCart.toCheckoutCart()
        _cartState.value = checkoutCart
        _displayCartState.value = checkoutCart.toDisplayCart()
    }

    private suspend fun CheckoutCart.toDisplayCart(selectedDeliveryHandle: String? = null): CheckoutDisplayCart {
        val subtotal = cost.subtotalAmount.amount
        val total = cost.totalAmount.amount
        val tax = cost.totalTaxAmount?.amount ?: BigDecimal.ZERO
        val shippingAmount = if (selectedDeliveryHandle != null) {
            deliveryGroups.firstOrNull()?.deliveryOptions?.find { it.handle == selectedDeliveryHandle }?.estimatedCost?.amount ?: BigDecimal.ZERO
        } else {
            defaultShippingOption?.estimatedCost?.amount ?: BigDecimal.ZERO
        }
        val discountAmount = subtotal.add(tax).add(shippingAmount).subtract(total)

        return CheckoutDisplayCart(
            id = id,
            displaySubtotal = convertPriceUseCase(subtotal.toDouble()),
            displayTax = convertPriceUseCase(tax.toDouble()),
            displayShipping = convertPriceUseCase(shippingAmount.toDouble()),
            displayDiscount = if (discountAmount > BigDecimal.ZERO) {
                "-" + convertPriceUseCase(discountAmount.toDouble())
            } else {
                null
            },
            displayTotal = convertPriceUseCase(total.toDouble()),
            lines = lines.map { line ->
                val lineTotal = line.price.amount.multiply(line.quantity.toBigDecimal())
                CheckoutDisplayLineItem(
                    id = line.id,
                    title = line.productTitle,
                    variantTitle = line.variantTitle,
                    quantity = line.quantity,
                    displayPrice = convertPriceUseCase(line.price.amount.toDouble()),
                    displayTotal = convertPriceUseCase(lineTotal.toDouble()),
                    imageUrl = line.imageUrl
                )
            },
            deliveryOptions = deliveryGroups.firstOrNull()?.deliveryOptions.orEmpty().map { option ->
                CheckoutDisplayDeliveryOption(
                    handle = option.handle,
                    title = option.title,
                    displayCost = convertPriceUseCase(option.estimatedCost.amount.toDouble())
                )
            }
        )
    }
}