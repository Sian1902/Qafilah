package com.example.qafilah.features.orders.presentation.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.ConvertPriceUseCase
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.features.orders.domain.model.Money
import com.example.qafilah.features.orders.domain.usecase.GetOrderByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val tokenProvider: TokenProvider,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailsUiState())
    val state: StateFlow<OrderDetailsUiState> = _state.asStateFlow()

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val token = tokenProvider.getToken()
            if (token == null) {
                _state.value = _state.value.copy(isLoading = false, error = "Not authenticated")
                return@launch
            }

            getOrderByIdUseCase(token, orderId)
                .onSuccess { order ->
                    if (order != null) {
                        val formattedOrder = order.copy(
                            totalPrice = formatMoney(order.totalPrice),
                            lineItems = order.lineItems.map { it.copy(price = formatMoney(it.price)) }
                        )
                        _state.value = _state.value.copy(isLoading = false, order = formattedOrder)
                    } else {
                        _state.value = _state.value.copy(isLoading = false, error = "Order not found")
                    }
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isLoading = false, error = error.message)
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
