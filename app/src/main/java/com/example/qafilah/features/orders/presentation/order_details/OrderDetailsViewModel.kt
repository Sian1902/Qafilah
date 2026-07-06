package com.example.qafilah.features.orders.presentation.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.currency.domain.usecase.ConvertPriceUseCase
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

    fun loadOrderDetails(
        orderId: String,
        orderNotFoundMessage: String,
        labels: OrderDetailLabels
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val order = getOrderByIdUseCase(orderId)
            if (order != null) {
                val uiModel = OrderDetailUiModel(
                    id = order.id,
                    orderNumber = "${labels.orderNumberPrefix} ${order.orderNumber}",
                    date = "${labels.datePrefix} ${order.processedAt.substringBefore("T")}",
                    status = order.fulfillmentStatus,
                    statusLabel = if (order.fulfillmentStatus == "FULFILLED") labels.fulfilledLabel else labels.processingLabel,
                    totalPrice = formatPrice(order.totalPrice.amount),
                    lineItems = order.lineItems.map { item ->
                        OrderLineItemUiModel(
                            title = item.title,
                            quantity = String.format(labels.quantityFormat, item.quantity),
                            price = formatPrice(item.price.amount),
                            variantTitle = item.variantTitle,
                            imageUrl = item.imageUrl
                        )
                    }
                )
                _state.value = _state.value.copy(isLoading = false, order = uiModel)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = orderNotFoundMessage)
            }
        }
    }

    private suspend fun formatPrice(amountStr: String): String {
        return try {
            val cleanAmountStr = amountStr.replace(",", ".")
            val amount = cleanAmountStr.toDoubleOrNull() ?: 0.0
            convertPriceUseCase(amount)
        } catch (e: Exception) {
            amountStr
        }
    }
}
