package com.example.qafilah.features.checkout.presentation.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import com.example.ui_kit.components.checkout.CheckoutProductCard
import com.example.ui_kit.components.checkout.CheckoutSummaryCard
import com.example.ui_kit.components.checkout.DeliveryOptionsRadioGroup
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutSummaryScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: CheckoutSharedViewModel,
    summaryViewModel: CheckoutSummaryViewModel = koinViewModel(),
    onNavigateToPayment: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()

    val localUiState by summaryViewModel.uiState.collectAsState()

    val finalUiState = buildSummaryUiState(
        cart = cart,
        isRecalculating = localUiState.isRecalculating,
        localSelectedHandle = localUiState.selectedDeliveryHandle
    )

    CheckoutSummaryContent(
        modifier = modifier,
        uiState = finalUiState,
        onShippingOptionSelected = { handle ->
            val groupId = cart!!.deliveryGroups.firstOrNull()?.id ?: return@CheckoutSummaryContent

            summaryViewModel.selectShippingOption(cart!!.id, groupId, handle) { updatedCart ->
                sharedViewModel.updateCartState(updatedCart)
            }
        },
        onProceedToPayment = onNavigateToPayment
    )
}

@Composable
fun CheckoutSummaryContent(
    modifier: Modifier = Modifier,
    uiState: CheckoutSummaryUIState,
    onShippingOptionSelected: (String) -> Unit,
    onProceedToPayment: () -> Unit
) {
    val cart = uiState.cart ?: return

    val uiKitDeliveryOptions = cart.deliveryGroups.firstOrNull()?.deliveryOptions?.map { option ->
        Triple(option.handle, option.title, "$${option.estimatedCost.amount}")
    } ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Order Summary",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(cart.lines) { lineItem ->
                CheckoutProductCard(
                    imageUrl = lineItem.imageUrl ?: "",
                    title = lineItem.productTitle,
                    variantTitle = lineItem.variantTitle,
                    price = "$${lineItem.price.amount}",
                    quantity = lineItem.quantity
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }

            if (uiKitDeliveryOptions.isNotEmpty()) {
                item {
                    DeliveryOptionsRadioGroup(
                        options = uiKitDeliveryOptions,
                        selectedOptionHandle = uiState.selectedDeliveryHandle,
                        onOptionSelected = onShippingOptionSelected
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }

            item {
                CheckoutSummaryCard(
                    subtotal = uiState.displaySubtotal,
                    discountAmount = uiState.displayDiscount,
                    tax = uiState.displayTax,
                    shippingAmount = uiState.displayShipping,
                    total = uiState.displayTotal,
                    isCalculating = uiState.isRecalculating
                )
            }
        }

        Button(
            onClick = onProceedToPayment,
            enabled = !uiState.isRecalculating && uiState.selectedDeliveryHandle != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Proceed to Payment",
                    style = MaterialTheme.typography.bodyLarge
                )

                if (uiState.isRecalculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "$${cart.cost.totalAmount.amount}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}