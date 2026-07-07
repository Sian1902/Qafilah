package com.example.qafilah.features.orders.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.usecase.GetOrdersUseCase
import com.example.qafilah.features.orders.domain.usecase.RefreshOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val refreshOrdersUseCase: RefreshOrdersUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUiState())
    val state: StateFlow<OrdersUiState> = _state.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            getOrdersUseCase().combine(
                state.map { it.labels }.distinctUntilChanged()
            ) { orders, labels ->
                orders to labels
            }.collectLatest { (orders, labels) ->
                if (labels != null) {
                    updateUiModels(orders, labels)
                }
            }
        }
    }

    private suspend fun updateUiModels(orders: List<Order>, labels: OrderLabels) {
        val uiModels = orders.map { order ->
            val formattedPrice = try {
                val cleanAmountStr = order.totalPrice.amount.replace(",", ".")
                val amount = cleanAmountStr.toDoubleOrNull() ?: 0.0
                convertPriceUseCase(amount)
            } catch (e: Exception) {
                "${order.totalPrice.amount} ${order.totalPrice.currencyCode}"
            }

            val statusLabel = when (order.fulfillmentStatus) {
                "FULFILLED" -> labels.fulfilledLabel
                "UNFULFILLED" -> labels.processingLabel
                else -> order.fulfillmentStatus
            }

            OrderUiModel(
                id = order.id,
                orderNumber = "${labels.orderNumberPrefix} ${order.orderNumber}",
                date = "${labels.datePrefix} ${order.processedAt.substringBefore("T")}",
                totalPrice = formattedPrice,
                status = order.fulfillmentStatus,
                statusLabel = statusLabel
            )
        }
        _state.value = _state.value.copy(orders = uiModels)
    }

    fun loadOrders(
        unknownErrorMessage: String,
        labels: OrderLabels
    ) {
        _state.value = _state.value.copy(isLoading = true, error = null, labels = labels)

        viewModelScope.launch {
            refreshOrdersUseCase()
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
