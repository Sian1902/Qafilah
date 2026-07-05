package com.example.qafilah.features.orders.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.ConvertPriceUseCase
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.features.orders.domain.model.Money
import com.example.qafilah.features.orders.domain.usecase.GetOrdersUseCase
import com.example.qafilah.features.orders.domain.usecase.RefreshOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val refreshOrdersUseCase: RefreshOrdersUseCase,
    private val tokenProvider: TokenProvider,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUiState())
    val state: StateFlow<OrdersUiState> = _state.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            getOrdersUseCase().collectLatest { orders ->
                val formattedOrders = orders.map { order ->
                    try {
                        val cleanAmountStr = order.totalPrice.amount.replace(",", ".")
                        val amount = cleanAmountStr.toDoubleOrNull() ?: 0.0
                        val formattedPrice = convertPriceUseCase(amount)
                        val parts = formattedPrice.trim().split(" ")
                        if (parts.size >= 2) {
                            val firstPartAsDouble = parts[0].replace(",", ".").toDoubleOrNull()
                            if (firstPartAsDouble != null) {
                                order.copy(totalPrice = Money(parts[0], parts[1]))
                            } else {
                                order.copy(totalPrice = Money(parts[1], parts[0]))
                            }
                        } else {
                            order
                        }
                    } catch (e: Exception) {
                        order
                    }
                }
                _state.value = _state.value.copy(orders = formattedOrders)
            }
        }
    }

    fun loadOrders(notAuthenticatedMessage: String, unknownErrorMessage: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val token = tokenProvider.getToken()
            if (token == null) {
                _state.value = _state.value.copy(isLoading = false, error = notAuthenticatedMessage)
                return@launch
            }

            refreshOrdersUseCase(token)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: unknownErrorMessage
                    )
                }
        }
    }
}
