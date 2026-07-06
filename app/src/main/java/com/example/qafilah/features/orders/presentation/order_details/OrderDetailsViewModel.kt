package com.example.qafilah.features.orders.presentation.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import com.example.qafilah.features.orders.domain.model.Money
import com.example.qafilah.features.orders.domain.usecase.GetOrderByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailsUiState())
    val state: StateFlow<OrderDetailsUiState> = _state.asStateFlow()

    fun loadOrderDetails(orderId: String, orderNotFoundMessage: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val order = getOrderByIdUseCase(orderId)
            if (order != null) {
                val formattedOrder = order.copy(
                    totalPrice = formatMoney(order.totalPrice),
                    lineItems = order.lineItems.map { it.copy(price = formatMoney(it.price)) }
                )
                _state.value = _state.value.copy(isLoading = false, order = formattedOrder)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = orderNotFoundMessage)
            }
        }
    }

    private suspend fun formatMoney(money: Money): Money {
        return try {
            val cleanAmountStr = money.amount.replace(",", ".")
            val amount = cleanAmountStr.toDoubleOrNull() ?: 0.0
            val formatted = convertPriceUseCase(amount)
            val parts = formatted.trim().split(" ")
            if (parts.size >= 2) {
                val firstPartAsDouble = parts[0].replace(",", ".").toDoubleOrNull()
                if (firstPartAsDouble != null) {
                    Money(parts[0], parts[1])
                } else {
                    Money(parts[1], parts[0])
                }
            } else money
        } catch (e: Exception) {
            money
        }
    }
}
