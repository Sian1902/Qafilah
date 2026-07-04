package com.example.qafilah.features.profile.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.core.currency.ConvertPriceUseCase
import com.example.qafilah.features.profile.domain.model.Money
import com.example.qafilah.features.profile.domain.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val tokenProvider: TokenProvider,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUiState())
    val state: StateFlow<OrdersUiState> = _state.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val mockOrders = listOf(
                Order(
                    id = "1",
                    orderNumber = 1001,
                    processedAt = "2024-07-01T10:00:00Z",
                    financialStatus = "PAID",
                    fulfillmentStatus = "FULFILLED",
                    totalPrice = Money("250.00", "USD"),
                    lineItems = emptyList()
                ),
                Order(
                    id = "2",
                    orderNumber = 1002,
                    processedAt = "2024-07-02T14:30:00Z",
                    financialStatus = "PAID",
                    fulfillmentStatus = "UNFULFILLED",
                    totalPrice = Money("120.00", "USD"),
                    lineItems = emptyList()
                )
            )

            val formattedOrders = mockOrders.map { order ->
                val formattedPrice = convertPriceUseCase(order.totalPrice.amount.toDouble())
                val parts = formattedPrice.split(" ")
                order.copy(
                    totalPrice = Money(parts[0], parts[1])
                )
            }
            
            _state.value = _state.value.copy(
                isLoading = false,
                orders = formattedOrders
            )
        }
    }
}
